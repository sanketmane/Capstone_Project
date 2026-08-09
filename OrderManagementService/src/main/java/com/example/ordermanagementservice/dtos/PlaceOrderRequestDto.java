package com.example.ordermanagementservice.dtos;

import com.example.ordermanagementservice.models.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

// Place Order request: cart items/total are fetched from CartService, not supplied here.
@Getter
@Setter
public class PlaceOrderRequestDto {
    private Long userId;
    private DeliveryAddressDto deliveryAddress;
    private PaymentMethod paymentMethod;
    private String email;
}
