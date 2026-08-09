package com.example.ordermanagementservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryAddressDto {
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String phoneNumber;
}
