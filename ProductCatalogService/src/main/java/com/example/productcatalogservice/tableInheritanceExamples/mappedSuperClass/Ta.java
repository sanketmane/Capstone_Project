package com.example.productcatalogservice.tableInheritanceExamples.mappedSuperClass;

import jakarta.persistence.Entity;

@Entity(name = "msc_ta")
public class Ta extends User {
    private Double ratings;
}
