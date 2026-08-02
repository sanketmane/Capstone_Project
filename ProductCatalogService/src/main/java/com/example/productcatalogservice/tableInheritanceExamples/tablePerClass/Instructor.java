package com.example.productcatalogservice.tableInheritanceExamples.tablePerClass;

import jakarta.persistence.Entity;

@Entity(name = "tpc_instructor")
public class Instructor extends User {
    private String company;
}
