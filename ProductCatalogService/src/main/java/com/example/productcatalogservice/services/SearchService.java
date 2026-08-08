package com.example.productcatalogservice.services;

import com.example.productcatalogservice.documents.ProductDocument;
import com.example.productcatalogservice.dtos.ProductDto;
import com.example.productcatalogservice.dtos.ProductMapper;
import com.example.productcatalogservice.dtos.SortParam;
import com.example.productcatalogservice.dtos.SortType;
import com.example.productcatalogservice.repos.ProductSearchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService implements ISearchService {

    @Autowired
    private ProductSearchRepo productSearchRepo;

    @Override
    public Page<ProductDto> searchProducts(String searchString,
                                        Integer pageNumber,
                                        Integer pageSize,
                                        List<SortParam> sortParamList) {
        Sort sort = null;
        // first get the initial sorting criteria from sortParamList
        // if the sorting type is asc, then use the default sort behavior otherwise do descending sort
        if (!sortParamList.isEmpty()) { 
            if (sortParamList.get(0).getSortType().equals(SortType.ASC))
                sort = Sort.by(sortParamList.get(0).getSortCriteria()); // default sorting is ascending
            else
                sort = Sort.by(sortParamList.get(0).getSortCriteria()).descending();

            // Once primary sorting behavior is complete, go over remaining sorting criteria's and types.
            for(int i=1;i<sortParamList.size();i++) {
                if (sortParamList.get(i).getSortType().equals(SortType.ASC))
                    sort = sort.and(Sort.by(sortParamList.get(i).getSortCriteria()));
                else
                    sort = sort.and(Sort.by(sortParamList.get(i).getSortCriteria()).descending());
            }
        }

        // Elasticsearch multi_match query gives full text search
        // across name and description, instead of a plain SQL LIKE match.
        Page<ProductDocument> productPage = productSearchRepo.searchByNameOrDescription(
                searchString, PageRequest.of(pageNumber, pageSize, sort));
        return productPage.map(ProductMapper::toDto);
    }
}

