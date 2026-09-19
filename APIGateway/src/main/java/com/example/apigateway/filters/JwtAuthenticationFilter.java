package main.java.com.example.apigateway.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;

/**
 * Validates the existing UserManagementService HS256 JWT once at the gateway.
 * Public routes (auth bootstrap/reset, payment webhooks) bypass validation and are
 * forwarded untouched so webhook signature verification downstream still works.
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final List<String> PUBLIC_PATH_PATTERNS = List.of(
            "/api/auth/signup",
            "/api/auth/login",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/api/payments/webhook/**"
    );

    private static final String ISSUER = "scaler";

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final SecretKey secretKey;

    public JwtAuthenticationFilter(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (isPublic(path)) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange.getRequest());
        if (token == null || !isValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPublic(String path) {
        return PUBLIC_PATH_PATTERNS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    // Compatibility: profile/business endpoints expect a raw "token" header;
    // login's Set-Cookie value is also the raw token (not a "name=value" cookie).
    private String extractToken(ServerHttpRequest request) {
        String tokenHeader = request.getHeaders().getFirst("token");
        if (tokenHeader != null && !tokenHeader.isBlank()) {
            return tokenHeader;
        }
        String cookieHeader = request.getHeaders().getFirst("Cookie");
        if (cookieHeader != null && !cookieHeader.isBlank()) {
            return cookieHeader;
        }
        return null;
    }

    private boolean isValid(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
            if (!ISSUER.equals(claims.getIssuer())) {
                return false;
            }
            Long expiresAt = claims.get("exp", Long.class);
            return expiresAt != null && expiresAt >= System.currentTimeMillis();
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
