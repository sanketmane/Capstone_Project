package com.example.emailservice.dtos;

import lombok.Getter;
import lombok.Setter;

// Kafka payload for "order.placed", mirrors OrderManagementService's event shape.
@Getter
@Setter
public class OrderPlacedEventDto {
    private Long orderId;
    private Long userId;
    private String email;
    private Double totalAmount;
    private Integer itemCount;
}
