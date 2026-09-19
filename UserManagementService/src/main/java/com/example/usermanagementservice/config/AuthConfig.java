package com.example.usermanagementservice.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;

@Configuration
public class AuthConfig {

    // Base64-encoded HS256 key, must be shared with APIGateway so it can validate the same tokens.
    @Value("${jwt.secret}")
    private String jwtSecretBase64;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // The below code snippet needs to be added because
    // the spring-boot-starter-security dependency brings up a default login page
    // which causes wrong response to be sent after making API call from postman
    // Adding the below turns off that default behavior of adding login page.

// Commenting out the below implementation of SecurityFilterChain as it blocks loading of Spring Auth server
    // which also has its own implementation of SecurityFilterChain in SecurityConfig class
    // Enable this for Kafka testing and disable the one in SecurityConfig
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.cors().disable();
//        httpSecurity.csrf().disable();
//        httpSecurity.authorizeHttpRequests(authorize->authorize.anyRequest().permitAll());
//        return httpSecurity.build();
//    }


    @Bean
    public SecretKey secretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
