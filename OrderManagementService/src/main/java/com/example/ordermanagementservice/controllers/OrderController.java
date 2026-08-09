package com.example.ordermanagementservice.controllers;

import com.example.ordermanagementservice.dtos.OrderDto;
import com.example.ordermanagementservice.dtos.PlaceOrderRequestDto;
import com.example.ordermanagementservice.dtos.UpdateOrderStatusRequestDto;
import com.example.ordermanagementservice.mappers.OrderMapper;
import com.example.ordermanagementservice.models.Order;
import com.example.ordermanagementservice.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping()
    public ResponseEntity<OrderDto> placeOrder(@RequestBody PlaceOrderRequestDto requestDto) {
        Order order = orderService.placeOrder(requestDto);
        return new ResponseEntity<>(OrderMapper.toDto(order), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> getOrderHistory(@PathVariable("userId") Long userId) {
        List<Order> orders = orderService.getOrderHistory(userId);
        List<OrderDto> orderDtos = new ArrayList<>();
        for (Order order : orders) {
            orderDtos.add(OrderMapper.toDto(order));
        }
        return ResponseEntity.ok(orderDtos);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable("orderId") Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(OrderMapper.toDto(order));
    }

    @PatchMapping("/{orderId}/status")
    // Why PatchMapping? Because we are partially updating the order resource, specifically its status.
    // So why not PutMapping? Because PutMapping is generally used for full updates, 
    // where the entire resource is replaced.
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable("orderId") Long orderId,
                                                       @RequestBody UpdateOrderStatusRequestDto requestDto) {
        Order order = orderService.updateOrderStatus(orderId, requestDto.getStatus());
        return ResponseEntity.ok(OrderMapper.toDto(order));
    }
}
