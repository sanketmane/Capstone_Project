package com.example.productcatalogservice.repos;

import com.example.productcatalogservice.documents.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSearchRepo extends ElasticsearchRepository<ProductDocument, Long> {

    // multi_match runs the search term against both fields and ranks results
    // by relevance, which is what gives us true full text search (unlike a SQL LIKE query).
    @Query("""
            {
              "multi_match": {
                "query": "?0",
                "fields": ["name", "description"]
              }
            }
            """)
    Page<ProductDocument> searchByNameOrDescription(String searchString, Pageable pageable);
}
