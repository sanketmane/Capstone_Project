package com.example.ordermanagementservice.models;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// Indicates that this class can be embedded in other entities, 
// which means its fields will be stored in the same table 
// as the entity that embeds it. 
// E.g. if an Order entity has a DeliveryAddress field, 
// the columns of DeliveryAddress will be part of the Order table.
@Embeddable 
public class DeliveryAddress {
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String phoneNumber;
}
