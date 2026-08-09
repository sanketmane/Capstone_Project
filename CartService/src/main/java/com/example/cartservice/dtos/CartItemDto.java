package com.example.cartservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {
    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private Double subtotal;
}
