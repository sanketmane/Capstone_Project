package com.example.cartservice.dtos;

import com.example.cartservice.models.OrderStatus;
import com.example.cartservice.models.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class OrderDto {
    private String id;
    private Long userId;
    private List<CartItemDto> items;
    private Double totalAmount;
    private DeliveryAddressDto deliveryAddress;
    private PaymentMethod paymentMethod;
    private Date orderedAt;
    private OrderStatus status;
}
