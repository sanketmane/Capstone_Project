package com.example.cartservice.mappers;

import com.example.cartservice.dtos.CartDto;
import com.example.cartservice.dtos.CartItemDto;
import com.example.cartservice.models.Cart;
import com.example.cartservice.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartMapper {

    private CartMapper() {
    }

    public static CartItemDto toDto(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }
        CartItemDto dto = new CartItemDto();
        dto.setProductId(cartItem.getProductId());
        dto.setProductName(cartItem.getProductName());
        dto.setPrice(cartItem.getPrice());
        dto.setQuantity(cartItem.getQuantity());
        dto.setSubtotal(cartItem.getSubtotal());
        return dto;
    }

    public static CartDto toDto(Cart cart) {
        if (cart == null) {
            return null;
        }
        CartDto dto = new CartDto();
        dto.setUserId(cart.getUserId());
        List<CartItemDto> itemDtos = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            itemDtos.add(toDto(item));
        }
        dto.setItems(itemDtos);
        dto.setTotalPrice(cart.getTotalPrice());
        return dto;
    }
}

