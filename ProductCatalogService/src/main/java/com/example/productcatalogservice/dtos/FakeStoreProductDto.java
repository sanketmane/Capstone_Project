package com.example.productcatalogservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
// Need to implement Serializable specially for Redis to
// get data in Object form because Redis stores data in bytes form
public class FakeStoreProductDto implements Serializable {
    // attributes are determined based on the actual FakeStoreApi fields
    // https://fakestoreapi.com/products/1
    // we will exclude the ratings part here as it is not imp from our perspective
    private Long id;
    private String title;
    private Double price;
    private String description;
    private String category;
    private String image;
}
