package com.example.apigateway;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

/**
 * Verifies gateway routing/forwarding behavior against a stubbed backend:
 * protected route JWT enforcement, header/cookie forwarding, and unmodified
 * webhook body + signature passthrough.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "jwt.secret=A+yhkQ/M9wjj8byALCGrxDcEkb8rkl/pPfl1hWUuz7g=")
class GatewayRoutingTest {

    private static final WireMockServer WIRE_MOCK = new WireMockServer(0);

    @BeforeAll
    static void startWireMock() {
        WIRE_MOCK.start();
    }

    @AfterAll
    static void stopWireMock() {
        WIRE_MOCK.stop();
    }

    @BeforeEach
    void resetWireMock() {
        WIRE_MOCK.resetAll();
    }

    @DynamicPropertySource
    static void wireMockUrl(DynamicPropertyRegistry registry) {
        registry.add("wiremock.baseUrl", () -> "http://localhost:" + WIRE_MOCK.port());
    }

    @LocalServerPort
    private int port;

    @Autowired
    private SecretKey secretKey;

    private WebTestClient webTestClient() {
        return WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @TestConfiguration
    static class TestRoutesConfig {
        @Bean
        public RouteLocator testRoutes(RouteLocatorBuilder builder,
                                        org.springframework.core.env.Environment env) {
            String baseUrl = env.getProperty("wiremock.baseUrl");
            return builder.routes()
                    .route("test-cart", r -> r.path("/api/cart/**").uri(baseUrl))
                    .route("test-webhook", r -> r.path("/api/payments/webhook/**").uri(baseUrl))
                    .build();
        }
    }

    @Test
    void protectedRoute_missingToken_returns401_withoutHittingBackend() {
        webTestClient().get().uri("/api/cart/1")
                .exchange()
                .expectStatus().isUnauthorized();

        WIRE_MOCK.verify(0, WireMock.getRequestedFor(urlPathMatching("/api/cart/1")));
    }

    @Test
    void protectedRoute_validToken_isForwarded_withTokenHeaderPreserved() {
        WIRE_MOCK.stubFor(get(urlPathMatching("/api/cart/1"))
                .willReturn(aResponse().withStatus(200).withBody("{\"userId\":1}")));

        String token = buildToken();

        webTestClient().get().uri("/api/cart/1")
                .header("token", token)
                .exchange()
                .expectStatus().isOk();

        WIRE_MOCK.verify(WireMock.getRequestedFor(urlPathMatching("/api/cart/1"))
                .withHeader("token", equalTo(token)));
    }

    @Test
    void webhook_isPublic_andBodyAndSignatureHeaderArePreserved() {
        String payload = "{\"type\":\"checkout.session.completed\"}";
        String signature = "t=123,v1=abcdef";
        WIRE_MOCK.stubFor(post(urlPathMatching("/api/payments/webhook/stripe"))
                .willReturn(aResponse().withStatus(200)));

        webTestClient().post().uri("/api/payments/webhook/stripe")
                .header("Stripe-Signature", signature)
                .bodyValue(payload)
                .exchange()
                .expectStatus().isOk();

        WIRE_MOCK.verify(WireMock.postRequestedFor(urlPathMatching("/api/payments/webhook/stripe"))
                .withHeader("Stripe-Signature", equalTo(signature))
                .withRequestBody(equalTo(payload)));
    }

    private String buildToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", "scaler");
        claims.put("id", 1L);
        claims.put("access", java.util.List.of());
        claims.put("gen", System.currentTimeMillis());
        claims.put("exp", System.currentTimeMillis() + 3600_000L);
        return Jwts.builder().claims(claims).signWith(secretKey).compact();
    }
}
