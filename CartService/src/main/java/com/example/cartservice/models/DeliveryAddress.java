package com.example.cartservice.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryAddress {
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String phoneNumber;
}
