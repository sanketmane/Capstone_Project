package com.example.cartservice.consumers;

import com.example.cartservice.dtos.OrderPlacedEventDto;
import com.example.cartservice.services.CartService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderPlacedConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CartService cartService;

    // Kafka listener for order placed events
    // Kafka clears the cart when an order is placed, so that the user doesn't have to do it manually.
    @KafkaListener(topics = "order.placed", groupId = "cartService")
    public void handleOrderPlaced(String message) {
        try {
            OrderPlacedEventDto event = objectMapper.readValue(message, OrderPlacedEventDto.class);
            cartService.clearCart(event.getUserId());
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }
}
