package com.example.productcatalogservice.dtos;

import com.example.productcatalogservice.documents.ProductDocument;
import com.example.productcatalogservice.models.Category;
import com.example.productcatalogservice.models.Product;

// Shared entity <-> DTO conversion, 
// reused by ProductController, SearchService and CategoryController
public class ProductMapper {

    private ProductMapper() {
    }

    // Builds the Elasticsearch document that gets indexed alongside the Product row in MySQL
    public static ProductDocument toDocument(Product product) {
        if (product == null) {
            return null;
        }
        ProductDocument productDocument = new ProductDocument();
        productDocument.setId(product.getId());
        productDocument.setName(product.getName());
        productDocument.setDescription(product.getDescription());
        productDocument.setPrice(product.getPrice());
        productDocument.setImageUrl(product.getImageUrl());
        if (product.getCategory() != null) {
            productDocument.setCategoryId(product.getCategory().getId());
            productDocument.setCategoryName(product.getCategory().getName());
        }
        return productDocument;
    }

    public static ProductDto toDto(ProductDocument productDocument) {
        if (productDocument == null) {
            return null;
        }
        ProductDto productDto = new ProductDto();
        productDto.setId(productDocument.getId());
        productDto.setName(productDocument.getName());
        productDto.setDescription(productDocument.getDescription());
        productDto.setPrice(productDocument.getPrice());
        productDto.setImageUrl(productDocument.getImageUrl());
        if (productDocument.getCategoryId() != null) {
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setId(productDocument.getCategoryId());
            categoryDto.setName(productDocument.getCategoryName());
            productDto.setCategory(categoryDto);
        }
        return productDto;
    }

    public static ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setImageUrl(product.getImageUrl());
        productDto.setCategory(toDto(product.getCategory()));
        return productDto;
    }

    public static Product toEntity(ProductDto productDto) {
        if (productDto == null) {
            return null;
        }
        Product product = new Product();
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImageUrl(productDto.getImageUrl());
        product.setCategory(toEntity(productDto.getCategory()));
        return product;
    }

    public static CategoryDto toDto(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setDescription(category.getDescription());
        return categoryDto;
    }

    public static Category toEntity(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        return category;
    }
}
