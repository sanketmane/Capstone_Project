package com.example.ordermanagementservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// Deserialization target for CartServiceClient's GET /api/cart/{userId} response.
@Getter
@Setter
public class CartDto {
    private Long userId;
    private List<CartItemDto> items;
    private Double totalPrice;
}
