package com.example.cartservice.dtos;

import lombok.Getter;
import lombok.Setter;

// Minimal local copy of ProductCatalogService's ProductDto - only the fields CartService needs
@Getter
@Setter
public class ProductDto {
    private Long id;
    private String name;
    private Double price;
}
