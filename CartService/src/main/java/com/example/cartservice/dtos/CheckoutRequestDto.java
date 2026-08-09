package com.example.cartservice.dtos;

import com.example.cartservice.models.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequestDto {
    private DeliveryAddressDto deliveryAddress;
    private PaymentMethod paymentMethod;
}
