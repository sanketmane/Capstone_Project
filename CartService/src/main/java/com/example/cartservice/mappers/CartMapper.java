package com.example.cartservice.mappers;

import com.example.cartservice.dtos.CartDto;
import com.example.cartservice.dtos.CartItemDto;
import com.example.cartservice.dtos.DeliveryAddressDto;
import com.example.cartservice.dtos.OrderDto;
import com.example.cartservice.models.Cart;
import com.example.cartservice.models.CartItem;
import com.example.cartservice.models.DeliveryAddress;
import com.example.cartservice.models.Order;

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

    public static DeliveryAddressDto toDto(DeliveryAddress address) {
        if (address == null) {
            return null;
        }
        DeliveryAddressDto dto = new DeliveryAddressDto();
        dto.setLine1(address.getLine1());
        dto.setLine2(address.getLine2());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setZipCode(address.getZipCode());
        dto.setCountry(address.getCountry());
        dto.setPhoneNumber(address.getPhoneNumber());
        return dto;
    }

    public static DeliveryAddress toEntity(DeliveryAddressDto dto) {
        if (dto == null) {
            return null;
        }
        DeliveryAddress address = new DeliveryAddress();
        address.setLine1(dto.getLine1());
        address.setLine2(dto.getLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setZipCode(dto.getZipCode());
        address.setCountry(dto.getCountry());
        address.setPhoneNumber(dto.getPhoneNumber());
        return address;
    }

    public static OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        List<CartItemDto> itemDtos = new ArrayList<>();
        for (CartItem item : order.getItems()) {
            itemDtos.add(toDto(item));
        }
        dto.setItems(itemDtos);
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDeliveryAddress(toDto(order.getDeliveryAddress()));
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setOrderedAt(order.getOrderedAt());
        dto.setStatus(order.getStatus());
        return dto;
    }
}
