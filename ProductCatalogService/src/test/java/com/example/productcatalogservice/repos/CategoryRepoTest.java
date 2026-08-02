package com.example.productcatalogservice.repos;

import com.example.productcatalogservice.models.Category;
import com.example.productcatalogservice.models.Product;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryRepoTest {

    @Autowired
    private CategoryRepo categoryRepo;

    @Test
    @Transactional
    // if there is a save operation happening, it will also do rollback so we don't need to save for test purposes
    public void testLoading() {
        // get() returns the value inside Optional; throws NoSuchElementException if empty
        Category category = categoryRepo.findById(1L).get();
//        for(Product product : category.getProducts()) {
//            System.out.println(product.getName());
//        }

    }

    @Test
    @Transactional
    public void testNPlusOneProblem() {
        List<Category> categoryList = categoryRepo.findAll();
        for (Category category : categoryList) {
            for (Product product : category.getProducts()) {
                System.out.println(product.getName());
            }
        }
    }
}