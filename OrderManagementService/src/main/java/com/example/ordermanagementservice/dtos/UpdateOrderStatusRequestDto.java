package com.example.ordermanagementservice.dtos;

import com.example.ordermanagementservice.models.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequestDto {
    private OrderStatus status;
}
