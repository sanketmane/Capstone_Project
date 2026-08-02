package com.example.productcatalogservice.services;

import com.example.productcatalogservice.models.Product;

import java.util.List;

public interface IProductService {
    // ProductService should use Product for input/output
    // Refer to the MVC arch diagram in the lecture
    List<Product> getAllProductDetails();
    Product getProductById(Long id);
    Product createProduct(Product product);  // post request
    Product replaceProduct(Long id, Product product); //put request
    Product deleteProduct(Long productId);
}
