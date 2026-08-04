package com.example.productcatalogservice.controllers;

import com.example.productcatalogservice.dtos.CategoryDto;
import com.example.productcatalogservice.dtos.ProductDto;
import com.example.productcatalogservice.dtos.SearchRequestDto;
import com.example.productcatalogservice.services.ISearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class SearchControllerTest {

    @Autowired
    private SearchController searchController;

    @MockBean
    private ISearchService searchService;

    @Test
    public void TestSearchProducts_ReturnsMatchingProductDtos() {
        //Arrange
        ProductDto productDto = new ProductDto();
        productDto.setId(1L);
        productDto.setName("Iphone 15");
        productDto.setDescription("Latest Apple phone");
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("phones");
        productDto.setCategory(categoryDto);

        Page<ProductDto> productDtoPage = new PageImpl<>(List.of(productDto), PageRequest.of(0, 10), 1);
        when(searchService.searchProducts(eq("phone"), eq(0), eq(10), anyList())).thenReturn(productDtoPage);

        SearchRequestDto request = new SearchRequestDto();
        request.setSearchString("phone");
        request.setPageNumber(0);
        request.setPageSize(10);

        //Act
        Page<ProductDto> response = searchController.searchProducts(request);

        //Assert
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Iphone 15", response.getContent().get(0).getName());
        assertEquals("phones", response.getContent().get(0).getCategory().getName());
    }
}
