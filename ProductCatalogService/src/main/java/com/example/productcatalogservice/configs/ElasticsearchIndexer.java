package com.example.productcatalogservice.configs;

import com.example.productcatalogservice.documents.ProductDocument;
import com.example.productcatalogservice.dtos.ProductMapper;
import com.example.productcatalogservice.models.Product;
import com.example.productcatalogservice.repos.ProductRepo;
import com.example.productcatalogservice.repos.ProductSearchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// Bulk-loads products already in MySQL into Elasticsearch on startup, so search
// works against data that existed before Elasticsearch was wired up.
// Runs only when the index is empty; ongoing writes are kept in sync by StorageProductService.
@Component
public class ElasticsearchIndexer implements ApplicationRunner {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ProductSearchRepo productSearchRepo;

    @Override
    public void run(ApplicationArguments args) {
        if (productSearchRepo.count() > 0) {
            return;
        }
        List<ProductDocument> documents = new ArrayList<>();
        for (Product product : productRepo.findAll()) {
            documents.add(ProductMapper.toDocument(product));
        }
        productSearchRepo.saveAll(documents);
    }
}
