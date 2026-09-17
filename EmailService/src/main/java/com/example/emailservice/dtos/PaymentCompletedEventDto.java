package com.example.emailservice.dtos;

import lombok.Getter;
import lombok.Setter;

// Kafka payload for "payment.completed", published by PaymentService after a webhook confirms payment
@Getter
@Setter
public class PaymentCompletedEventDto {
    private String orderId;
    private String email;
    private Long amount;
    private String gateway;
    private String gatewayReference;
}
