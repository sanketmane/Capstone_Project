package com.example.paymentservice.clients;

import com.example.paymentservice.exceptions.OrderServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

// updates the order status in OrderManagementService once a payment succeeds
@Component
public class OrderServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public OrderServiceClient(RestTemplate restTemplate,
                               @Value("${order-service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public void updateOrderStatus(Long orderId, String status) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("status", status), headers);
            restTemplate.patchForObject(baseUrl + "/{orderId}/status", request, Void.class, orderId);
        } catch (RestClientException ex) {
            throw new OrderServiceUnavailableException("OrderManagementService is unavailable", ex);
        }
    }
}
