package com.example.productcatalogservice.services;

import com.example.productcatalogservice.models.Product;
import com.example.productcatalogservice.repos.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("sps") // means we declare this class as a Service component with a specific name "sps"
// Now, you call this class name through the Qualifier annotation in the ProductController class if you want.
// Use either Qualifier or the below Primary option to select the object/bean for IProductService.
@Primary // means this class is used by default for IProductService object creation.
// which is used in the ProductController class
public class StorageProductService implements IProductService{

    @Autowired
    private ProductRepo productRepo;


    @Override
    public List<Product> getAllProductDetails() {
        return productRepo.findAll();
    }

    @Override
    public Product getProductById(Long productId) {
        Optional<Product> productOptional = productRepo.findById(productId);
        if(productOptional.isPresent()){
            return productOptional.get();
        }
        return null;
    }

    @Override
    public Product createProduct(Product product) {
        Optional<Product> productOptional = productRepo.findById(product.getId());
        if(productOptional.isPresent()){
            return productOptional.get();
        }
        return productRepo.save(product);
    }

    @Override
    public Product replaceProduct(Long id, Product product) {
        return productRepo.save(product);
    }

    @Override
    public Product deleteProduct(Long productId) {
        return null;
    }
}
