package com.example.ordermanagementservice.mappers;

import com.example.ordermanagementservice.dtos.CartItemDto;
import com.example.ordermanagementservice.dtos.DeliveryAddressDto;
import com.example.ordermanagementservice.dtos.OrderDto;
import com.example.ordermanagementservice.dtos.OrderItemDto;
import com.example.ordermanagementservice.models.DeliveryAddress;
import com.example.ordermanagementservice.models.Order;
import com.example.ordermanagementservice.models.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderMapper {

    private OrderMapper() {
    }

    public static OrderItem toOrderItem(CartItemDto cartItemDto) {
        OrderItem item = new OrderItem();
        item.setProductId(cartItemDto.getProductId());
        item.setProductName(cartItemDto.getProductName());
        item.setPrice(cartItemDto.getPrice());
        item.setQuantity(cartItemDto.getQuantity());
        item.setSubtotal(cartItemDto.getSubtotal());
        return item;
    }

    public static OrderItemDto toDto(OrderItem orderItem) {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(orderItem.getProductId());
        dto.setProductName(orderItem.getProductName());
        dto.setPrice(orderItem.getPrice());
        dto.setQuantity(orderItem.getQuantity());
        dto.setSubtotal(orderItem.getSubtotal());
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

    public static OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        List<OrderItemDto> itemDtos = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            itemDtos.add(toDto(item));
        }
        dto.setItems(itemDtos);
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDeliveryAddress(toDto(order.getDeliveryAddress()));
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }
}
