package com.example.productcatalogservice.controllers;

import com.example.productcatalogservice.dtos.CategoryDto;
import com.example.productcatalogservice.models.Category;
import com.example.productcatalogservice.services.ICategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class CategoryControllerTest {

    @Autowired
    private CategoryController categoryController;

    @MockBean
    private ICategoryService categoryService;

    @Test
    public void TestGetAllCategories_RunSuccessfully() {
        //Arrange
        Category category = new Category();
        category.setId(1L);
        category.setName("phones");
        category.setDescription("Mobile phones");
        when(categoryService.getAllCategories()).thenReturn(List.of(category));

        //Act
        ResponseEntity<List<CategoryDto>> response = categoryController.getAllCategories();

        //Assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("phones", response.getBody().get(0).getName());
        assertEquals("Mobile phones", response.getBody().get(0).getDescription());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
