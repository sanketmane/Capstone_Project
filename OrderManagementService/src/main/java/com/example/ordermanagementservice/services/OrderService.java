package com.example.ordermanagementservice.services;

import com.example.ordermanagementservice.dtos.PlaceOrderRequestDto;
import com.example.ordermanagementservice.models.Order;
import com.example.ordermanagementservice.models.OrderStatus;

import java.util.List;

public interface OrderService {

    Order placeOrder(PlaceOrderRequestDto requestDto);

    Order getOrderById(Long orderId);

    List<Order> getOrderHistory(Long userId);

    Order updateOrderStatus(Long orderId, OrderStatus status);
}
