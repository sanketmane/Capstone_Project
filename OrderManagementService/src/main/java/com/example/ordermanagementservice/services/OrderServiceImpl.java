package com.example.ordermanagementservice.services;

import com.example.ordermanagementservice.clients.CartServiceClient;
import com.example.ordermanagementservice.clients.KafkaClient;
import com.example.ordermanagementservice.dtos.CartDto;
import com.example.ordermanagementservice.dtos.CartItemDto;
import com.example.ordermanagementservice.dtos.OrderPlacedEventDto;
import com.example.ordermanagementservice.dtos.PlaceOrderRequestDto;
import com.example.ordermanagementservice.exceptions.EmptyCartException;
import com.example.ordermanagementservice.exceptions.OrderNotFoundException;
import com.example.ordermanagementservice.mappers.OrderMapper;
import com.example.ordermanagementservice.models.Order;
import com.example.ordermanagementservice.models.OrderItem;
import com.example.ordermanagementservice.models.OrderStatus;
import com.example.ordermanagementservice.repos.OrderRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final String ORDER_PLACED_TOPIC = "order.placed";

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private CartServiceClient cartServiceClient;

    @Autowired
    private KafkaClient kafkaClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Order placeOrder(PlaceOrderRequestDto requestDto) {
        CartDto cart = cartServiceClient.getCart(requestDto.getUserId());
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cannot place an order with an empty cart");
        }

        Order order = new Order();
        order.setUserId(requestDto.getUserId());
        for (CartItemDto cartItemDto : cart.getItems()) {
            OrderItem item = OrderMapper.toOrderItem(cartItemDto);
            item.setOrder(order);
            order.getItems().add(item);
        }
        order.setTotalAmount(cart.getTotalPrice());
        order.setDeliveryAddress(OrderMapper.toEntity(requestDto.getDeliveryAddress()));
        order.setPaymentMethod(requestDto.getPaymentMethod());
        order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepo.save(order);

        publishOrderPlacedEvent(savedOrder, requestDto.getEmail());

        return savedOrder;
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found for id: " + orderId));
    }

    @Override
    public List<Order> getOrderHistory(Long userId) {
        return orderRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepo.save(order);
    }

    private void publishOrderPlacedEvent(Order order, String email) {
        OrderPlacedEventDto event = new OrderPlacedEventDto();
        event.setOrderId(order.getId());
        event.setUserId(order.getUserId());
        event.setEmail(email);
        event.setTotalAmount(order.getTotalAmount());
        event.setItemCount(order.getItems().size());
        try {
            kafkaClient.sendMessage(ORDER_PLACED_TOPIC, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }
}
