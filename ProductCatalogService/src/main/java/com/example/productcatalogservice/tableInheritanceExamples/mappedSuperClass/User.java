package com.example.productcatalogservice.tableInheritanceExamples.mappedSuperClass;

import jakarta.persistence.*;

import java.util.UUID;

// MapperSuperClass concept will be same to tablePerClass approach, except that
// for base class there is no table created.
@MappedSuperclass
// While using @MappedSuperclass the particular class should be abstract since it is a base class
public abstract class User {

    @Id
    private UUID id;
    private String email;
}
