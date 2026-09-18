package com.example.paymentservice.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced
    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        // HttpComponents factory ensures PATCH requests work (JDK's HttpURLConnection doesn't support PATCH)
        return restTemplateBuilder.requestFactory(() -> new HttpComponentsClientHttpRequestFactory()).build();
    }
}
