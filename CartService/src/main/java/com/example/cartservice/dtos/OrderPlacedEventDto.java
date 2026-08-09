package com.example.cartservice.dtos;

import lombok.Getter;
import lombok.Setter;

// Kafka payload for "order.placed", published by OrderManagementService; only userId is used here.
@Getter
@Setter
public class OrderPlacedEventDto {
    private Long orderId;
    private Long userId;
    private String email;
    private Double totalAmount;
    private Integer itemCount;
}
