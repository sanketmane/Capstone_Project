package com.example.ordermanagementservice.clients;

import com.example.ordermanagementservice.dtos.CartDto;
import com.example.ordermanagementservice.exceptions.CartServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

// Fetches the user's cart from CartService so OrderManagementService doesn't
// trust client-supplied items/prices when placing an order.
@Component
public class CartServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public CartServiceClient(RestTemplate restTemplate,
                              @Value("${cart-service.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public CartDto getCart(Long userId) {
        try {
            return restTemplate.getForObject(baseUrl + "/{userId}", CartDto.class, userId);
        } catch (RestClientException ex) {
            throw new CartServiceUnavailableException("CartService is unavailable", ex);
        }
    }
}
