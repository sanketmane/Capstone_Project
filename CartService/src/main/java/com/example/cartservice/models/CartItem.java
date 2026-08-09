package com.example.cartservice.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItem {
    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private Double subtotal;
}
