package com.example.productcatalogservice.repos;

import com.example.productcatalogservice.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // This annotation tells Spring: This class is intended to access the database (or any persistence store).
// Explanation of types in JpaRepository<Product, Long>
// Product - This means the table to which Product class is mapped to
// Long - DataType of the primary key for Product table, which is id and is of type Long.
public interface ProductRepo extends JpaRepository<Product, Long> {
    // Optional is used to represent an optional value that may or may not be present,
    // instead of returning or using null directly.
    Optional<Product> findById(Long id);

    // save() is from JpaRepository and checks if a row with say product exists,
    // if it does, then it overrides it with the new value;
    // if not, create a new row with that new value.
    Product save(Product product);

    // findAll() from JPA returns all rows.
    List<Product> findAll();

    // The below method is not directly part of JPA, but
    // JPA uses some intelligence behind the hood and creates
    // queries by understanding the method name and executes that query.
    List<Product> findAllByOrderByPriceDesc();

    // Similar to findAllByOrderByPriceDesc(), the below method is also not
    // part of JPA, but the difference here is, we are giving JPA the direct
    // SQL query it needs to execute rather than asking it to infer based on the
    // method name which it always cannot.
    // Also, input param to the query, p.id in this case is passed based on method
    // argument which is mapped through positional arguments(?1 - 1st position argument)
    // or named arguments(:method argument name)
    @Query("SELECT p.description from Product p where p.id=:id")
    String getMeDescriptionForProductId(Long id);

    // Query the search string in db and return a pageable response of Product
    Page<Product> findByNameEquals(String searchString, Pageable pageable);

}
