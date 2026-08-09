package com.example.cartservice.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
// @Document annotation specifies that this class is a MongoDB document and maps to the "carts" collection in the database.
@Document(collection = "carts") 
public class Cart {
    @Id
    private String id;
    @Indexed(unique = true) // @Indexed annotation creates a unique index on the userId field to ensure that each user can have only one cart in the database.
    private Long userId;
    private List<CartItem> items = new ArrayList<>();
    private Double totalPrice = 0.0;
    private Date createdAt;
    private Date updatedAt;
}
