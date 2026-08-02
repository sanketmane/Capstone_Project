package com.example.productcatalogservice.tableInheritanceExamples.noInheritanceSpecified;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity(name = "ni_ta")
public class Ta extends User {
    private Double ratings;
}
