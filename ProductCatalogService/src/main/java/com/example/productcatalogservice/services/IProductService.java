package com.example.productcatalogservice.services;

import com.example.productcatalogservice.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService {
    // ProductService should use Product for input/output
    // Refer to the MVC arch diagram in the lecture
    List<Product> getAllProductDetails();
    Product getProductById(Long id);
    Product createProduct(Product product);  // post request
    Product replaceProduct(Long id, Product product); //put request
    void deleteProduct(Long productId);
    Page<Product> getProductsByCategory(Long categoryId, Pageable pageable);
}
