package main.java.com.example.apigateway.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class JwtConfig {

    // Must match UserManagementService's jwt.secret so tokens issued there validate here.
    @Value("${jwt.secret}")
    private String jwtSecretBase64;

    @Bean
    public SecretKey secretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
