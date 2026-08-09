package com.example.cartservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequestDto {
    private Long productId;
    private Integer quantity;
}
