package com.example.cartservice.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Date;

@Getter
@Setter
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private Long userId;
    private List<CartItem> items;
    private Double totalAmount;
    private DeliveryAddress deliveryAddress;
    private PaymentMethod paymentMethod;
    private Date orderedAt;
    private OrderStatus status;
}
