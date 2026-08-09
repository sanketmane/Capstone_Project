package com.example.ordermanagementservice.dtos;

import com.example.ordermanagementservice.models.OrderStatus;
import com.example.ordermanagementservice.models.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private Long userId;
    private List<OrderItemDto> items;
    private Double totalAmount;
    private DeliveryAddressDto deliveryAddress;
    private PaymentMethod paymentMethod;
    private OrderStatus status;
    private Date createdAt;
}
