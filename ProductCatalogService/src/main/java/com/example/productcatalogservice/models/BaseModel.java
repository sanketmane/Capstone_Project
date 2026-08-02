package com.example.productcatalogservice.models;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass // This annotation allows the BaseModel fields to be added
// columns to the child classes(models)
public abstract class BaseModel {
    @Id // this annotation makes below id attribute as primary_key in the child class tables/models
    private Long id;
    private Date createdAt;
    private Date lastUpdatedAt;
    private State state;


}
