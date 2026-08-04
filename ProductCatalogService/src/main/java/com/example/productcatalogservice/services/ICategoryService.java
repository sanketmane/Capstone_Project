package com.example.productcatalogservice.services;

import com.example.productcatalogservice.models.Category;

import java.util.List;

public interface ICategoryService {
    List<Category> getAllCategories();
}
