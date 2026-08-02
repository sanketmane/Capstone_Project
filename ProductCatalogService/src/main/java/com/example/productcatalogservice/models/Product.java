package com.example.productcatalogservice.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Product extends BaseModel {
    private String name;
    private String description;
    private Double price;
    private String imageUrl;

    // @ManyToOne annotation signifies relationship of Product with Category which is M:1
    // CascadeType.ALL means say - if a category isn't present but a product with a new category
    // is being added then a new Category will be automatically created.
    @ManyToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    private Category category;
    private Boolean isPrimeSpecific; // Business Specific field which we don't want to expose to Seller or Buyer

    public Product() {
        // below setter methods are available because of lombok
        // Getter and Setter annotations in the BaseModel
        this.setCreatedAt(new Date());
        this.setLastUpdatedAt(new Date());
        this.setState(State.ACTIVE);
    }
}
