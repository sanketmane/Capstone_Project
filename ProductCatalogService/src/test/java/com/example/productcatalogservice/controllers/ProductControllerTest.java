package com.example.productcatalogservice.controllers;

import com.example.productcatalogservice.dtos.ProductDto;
import com.example.productcatalogservice.models.Product;
import com.example.productcatalogservice.services.IProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductControllerTest {

    @Autowired
    private ProductController productController;

    @MockBean
    // @Mockbean is used to mock the IProductService object
    // Why @Autowired wasn't used?
    // Ans:
    //  -   Spring would inject the real implementation of IProductService from the context.
    //  -   You wouldn’t be able to easily control its behavior in your test with Mockito.
    //  -   our test would become an integration test of both controller and service together,
    //      instead of a focused unit test of the controller.
    private IProductService productService;

    @Test
    public void TestGetProductById_OnValidId_RunSuccessfully() {
        //Arrange
        Long id = 2L;
        Product product = new Product();
        product.setId(id);
        product.setName("Iphone 15");
        product.setPrice(150000D);
        // following is how we mock service using Mockito
        // the below translates as: when you run "productService.getProductById(id)"
        // then return product object that you just created above.
        when(productService.getProductById(id)).thenReturn(product);

        //Act
        ResponseEntity<ProductDto> productDtoResponseEntity =
                productController.getProductById(id);

        //Assert
        assertNotNull(productDtoResponseEntity);
        assertNotNull(productDtoResponseEntity.getBody());
        assertEquals(id,productDtoResponseEntity.getBody().getId());
        assertEquals("Iphone 15",productDtoResponseEntity.getBody().getName());
        assertEquals(HttpStatus.OK,productDtoResponseEntity.getStatusCode());
    }


    @Test
    public void TestGetProductById_WithInvalidId_ResultsInIllegalArgumentException() {
        //Arrange
        Long id = -1L;

        //Act and Assert
        // assertThrows raises an exception in order to capture the exception you want to test
        // This exception can be stored and used later as can be seen below.
        Exception exception = assertThrows(IllegalArgumentException.class,()->productController.getProductById(id));
        assertEquals("Product Id cannot be less than 0",exception.getMessage());
    }

    @Test
    public void TestGetProductById_ServiceThrowsException_ResultsInSameException() {
        //Arrange
        // Here we are mocking productService.getProductById(id) to return a RuntimeException()
        Long id = 21L;
        when(productService.getProductById(id)).thenThrow(new RuntimeException());

        //Act and Assert
        assertThrows(RuntimeException.class,()->productController.getProductById(id));
    }

    @Test
    public void TestCreateProductWithValidInput_RunSuccessfully() {
        //Arrange
        ProductDto input = new ProductDto();
        input.setId(10L);
        input.setName("MacBook Air");
        input.setPrice(200000D);

        Product product = new Product();
        product.setId(10L);
        product.setName("MacBook Air");
        product.setPrice(200000D);

        // the reason we are passing any(Product.class) and not product we created here
        // because in the actual ProductController createProduct(), input product
        // object is created by the from(productDto)
        // So we say to mock below that you can accept any Product.class as input
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        //Act
        ProductDto responseProductDto  = productController.createProduct(input);

        //Assert
        assertNotNull(responseProductDto);
        assertEquals(10L,responseProductDto.getId());
        assertEquals("MacBook Air",responseProductDto.getName());
        assertEquals(200000D,responseProductDto.getPrice());
    }


}