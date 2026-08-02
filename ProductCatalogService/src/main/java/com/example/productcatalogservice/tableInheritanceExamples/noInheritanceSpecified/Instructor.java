package com.example.productcatalogservice.tableInheritanceExamples.noInheritanceSpecified;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity(name = "ni_instructor")
public class Instructor extends User {
    private String company;
}
