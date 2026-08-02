package com.example.productcatalogservice.repos;

import com.example.productcatalogservice.models.Category;
import com.example.productcatalogservice.models.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductRepoTest {

    @Autowired
    private ProductRepo productRepo;

    @Test
    public void testQueries() {
//      List<Product> productList = productRepo.findAllByOrderByPriceDesc();
//      for(Product product : productList) {
//          System.out.println(product.getPrice());
//      }
        System.out.println(productRepo.getMeDescriptionForProductId(1L)); //should return asus laptop
    }

    @Test
    // test method to dump data in aws rds instance or any mysql db as such
    // which you can connect to.
    public void insertDataIntoRds() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Iphone 16");
        product1.setPrice(150000D);
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("phones");
        product1.setCategory(category1);
        productRepo.save(product1);


        Product product2 = new Product();
        product2.setId(10L);
        product2.setName("Levis Tshirt");
        product2.setPrice(1500D);
        Category category2 = new Category();
        category2.setId(1000L);
        category2.setName("apparels");
        product2.setCategory(category2);
        productRepo.save(product2);

    }

}