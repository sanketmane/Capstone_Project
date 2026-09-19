package com.example.apigateway.filters;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private final SecretKey secretKey = Jwts.SIG.HS256.key().build();
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(secretKey);
    }

    @Test
    void publicRoute_bypassesValidation_withoutToken() {
        ServerWebExchange exchange = exchangeFor("/api/auth/login", null);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        filter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
        assertTrue(exchange.getResponse().getStatusCode() == null
                || exchange.getResponse().getStatusCode().is2xxSuccessful());
    }

    @Test
    void webhookRoute_bypassesValidation() {
        ServerWebExchange exchange = exchangeFor("/api/payments/webhook/stripe", null);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        filter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
    }

    @Test
    void protectedRoute_missingToken_returns401_withoutForwarding() {
        ServerWebExchange exchange = exchangeFor("/api/cart/1", null);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        filter.filter(exchange, chain).block();

        verifyNoInteractions(chain);
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void protectedRoute_malformedToken_returns401() {
        ServerWebExchange exchange = exchangeFor("/api/cart/1", "not-a-jwt");
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        filter.filter(exchange, chain).block();

        verifyNoInteractions(chain);
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void protectedRoute_expiredToken_returns401() {
        String expiredToken = buildToken(secretKey, System.currentTimeMillis() - 1000);
        ServerWebExchange exchange = exchangeFor("/api/cart/1", expiredToken);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        filter.filter(exchange, chain).block();

        verifyNoInteractions(chain);
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void protectedRoute_wrongSignature_returns401() {
        SecretKey otherKey = Jwts.SIG.HS256.key().build();
        String token = buildToken(otherKey, System.currentTimeMillis() + 3600_000L);
        ServerWebExchange exchange = exchangeFor("/api/cart/1", token);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        filter.filter(exchange, chain).block();

        verifyNoInteractions(chain);
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void protectedRoute_validToken_isForwarded() {
        String token = buildToken(secretKey, System.currentTimeMillis() + 3600_000L);
        ServerWebExchange exchange = exchangeFor("/api/cart/1", token);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        filter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
        assertTrue(exchange.getResponse().getStatusCode() == null
                || exchange.getResponse().getStatusCode().is2xxSuccessful());
    }

    private ServerWebExchange exchangeFor(String path, String token) {
        MockServerHttpRequest.BaseBuilder<?> builder = MockServerHttpRequest.get(path);
        if (token != null) {
            builder.header("token", token);
        }
        return MockServerWebExchange.from(builder.build());
    }

    private String buildToken(Key key, long expiresAtMillis) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("iss", "scaler");
        claims.put("id", 1L);
        claims.put("access", java.util.List.of());
        claims.put("gen", System.currentTimeMillis());
        claims.put("exp", expiresAtMillis);
        return Jwts.builder().claims(claims).signWith(key).compact();
    }
}
