package com.example.paymentservice.dtos;

import lombok.Getter;
import lombok.Setter;

// Kafka payload for "payment.completed", consumed by EmailService to send a confirmation email
@Getter
@Setter
public class PaymentCompletedEventDto {
    private String orderId;
    private String email;
    private Long amount;
    private PaymentGatewayType gateway;
    private String gatewayReference;
}
