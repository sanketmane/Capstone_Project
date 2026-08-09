package com.example.cartservice.services;

import com.example.cartservice.dtos.AddToCartRequestDto;
import com.example.cartservice.models.Cart;

public interface CartService {

    Cart getCart(Long userId);

    Cart addItem(Long userId, AddToCartRequestDto requestDto);

    Cart updateItemQuantity(Long userId, Long productId, Integer quantity);

    Cart removeItem(Long userId, Long productId);

    void clearCart(Long userId);
}
