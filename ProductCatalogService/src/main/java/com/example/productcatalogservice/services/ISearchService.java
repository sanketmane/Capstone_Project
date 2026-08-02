package com.example.productcatalogservice.services;

import com.example.productcatalogservice.dtos.SortParam;
import com.example.productcatalogservice.models.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ISearchService {
    Page<Product> searchProducts(String searchString,
                                 Integer pageNumber,
                                 Integer pageSize,
                                 List<SortParam> sortParamList);

}
