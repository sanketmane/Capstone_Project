package com.example.productcatalogservice.controllers;

import com.example.productcatalogservice.dtos.ProductDto;
import com.example.productcatalogservice.models.Product;
import com.example.productcatalogservice.services.IProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// As we will be testing actual API functionality,
// we need to annotate the class as with WebMvcTest and
// provide the actual underlying class that handles the functionality
@WebMvcTest(ProductController.class)
public class ProductControllerMvcTest {

    // to simulate a client we will use the below MockMvc object
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void TestGetAllProducts_ReturnsStatusOk() throws Exception {
        // Get the status after running /products
        mockMvc.perform(get("/products")).andExpect(status().isOk());
    }

    @Test
    public void TestGetAllProducts_RunsSuccessfully() throws Exception {
        // Arrange
        // Prepare product and productDto lists based on getAllProducts()
        Product product = new Product();
        product.setId(1L);
        product.setName("Iphone");
        product.setDescription("Iphone 16");
        List<Product> allProducts = new ArrayList<>();
        allProducts.add(product);

        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName("Iphone");
        productDto.setDescription("Iphone 16");
        List<ProductDto> productDtos = new ArrayList<>();
        productDtos.add(productDto);

        // Actual response
        // objectMapper does the serialization/deserialization of json for us.
        String expectedResponse = objectMapper.writeValueAsString(productDtos);

        when(productService.getAllProductDetails()).thenReturn(allProducts);
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    public void TestCreateProduct_RunsSuccessfully() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("Iphone");
        product.setDescription("Iphone 16");

        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName("Iphone");
        productDto.setDescription("Iphone 16");

        // since we create a new object in the mapping method from(), we need to
        // pass any() with Product.class. This is to declare that any product
        // is accepted.
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/products")
                        // we need to pass MediaType.APPLICATION_JSON explicitly
                        // to declare i/p as of type JSON otherwise it assumes octet/stream and test will fail
                        .contentType(MediaType.APPLICATION_JSON)
                        // pass json as string as i/p
                        .content(objectMapper.writeValueAsString(productDto)))
                        .andExpect(status().isOk())
                        .andExpect(content().string(objectMapper.writeValueAsString(productDto)));

    }
}
