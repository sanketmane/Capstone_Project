package com.example.ordermanagementservice.controllers;

import com.example.ordermanagementservice.dtos.PlaceOrderRequestDto;
import com.example.ordermanagementservice.dtos.UpdateOrderStatusRequestDto;
import com.example.ordermanagementservice.exceptions.EmptyCartException;
import com.example.ordermanagementservice.exceptions.OrderNotFoundException;
import com.example.ordermanagementservice.models.Order;
import com.example.ordermanagementservice.models.OrderStatus;
import com.example.ordermanagementservice.services.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void placeOrder_returnsCreatedWithOrderBody() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setTotalAmount(1000.0);
        order.setStatus(OrderStatus.PENDING);
        when(orderService.placeOrder(any(PlaceOrderRequestDto.class))).thenReturn(order);

        PlaceOrderRequestDto requestDto = new PlaceOrderRequestDto();
        requestDto.setUserId(1L);
        requestDto.setEmail("user@example.com");

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void placeOrder_returnsBadRequest_whenCartEmpty() throws Exception {
        when(orderService.placeOrder(any(PlaceOrderRequestDto.class)))
                .thenThrow(new EmptyCartException("Cannot place an order with an empty cart"));

        PlaceOrderRequestDto requestDto = new PlaceOrderRequestDto();
        requestDto.setUserId(1L);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrderHistory_returnsOkWithOrderList() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setStatus(OrderStatus.PENDING);
        when(orderService.getOrderHistory(1L)).thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(1));
    }

    @Test
    void getOrder_returnsOkWithOrderBody() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setStatus(OrderStatus.PENDING);
        when(orderService.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getOrder_returnsNotFound_whenOrderMissing() throws Exception {
        when(orderService.getOrderById(99L))
                .thenThrow(new OrderNotFoundException("Order not found for id: 99"));

        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOrderStatus_returnsOkWithUpdatedOrder() throws Exception {
        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setStatus(OrderStatus.COMPLETED);
        when(orderService.updateOrderStatus(anyLong(), any(OrderStatus.class))).thenReturn(order);

        UpdateOrderStatusRequestDto requestDto = new UpdateOrderStatusRequestDto();
        requestDto.setStatus(OrderStatus.COMPLETED);

        mockMvc.perform(patch("/api/orders/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}
