package com.example.paymentservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestDto {
    Long amount;
    String orderId;
    String phoneNumber;
    String name;
    String email;

}
