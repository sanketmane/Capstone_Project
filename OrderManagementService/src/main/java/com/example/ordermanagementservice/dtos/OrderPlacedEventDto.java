package com.example.ordermanagementservice.dtos;

import lombok.Getter;
import lombok.Setter;

// Kafka payload for the "order.placed" topic, consumed by CartService (clears cart)
// and EmailService (sends confirmation) via their own copies of this shape.
@Getter
@Setter
public class OrderPlacedEventDto {
    private Long orderId;
    private Long userId;
    private String email;
    private Double totalAmount;
    private Integer itemCount;
}
