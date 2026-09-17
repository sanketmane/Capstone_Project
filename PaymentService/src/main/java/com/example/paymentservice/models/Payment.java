package com.example.paymentservice.models;

import com.example.paymentservice.dtos.PaymentGatewayType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment extends BaseModel {
    private String orderId;

    @Enumerated(EnumType.STRING)
    private PaymentGatewayType gateway;

    private Long amount;
    private String email;
    private String name;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    // gateway-side identifier (Stripe PaymentLink id / Razorpay payment link id), used to trace webhook callbacks
    private String gatewayReference;

    public Payment() {
        this.setCreatedAt(new Date());
        this.setLastUpdatedAt(new Date());
        this.setState(State.ACTIVE);
    }
}
