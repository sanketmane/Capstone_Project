package com.example.productcatalogservice.services;

import com.example.productcatalogservice.dtos.ProductDto;
import com.example.productcatalogservice.dtos.SortParam;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ISearchService {
    Page<ProductDto> searchProducts(String searchString,
                                 Integer pageNumber,
                                 Integer pageSize,
                                 List<SortParam> sortParamList);

}
