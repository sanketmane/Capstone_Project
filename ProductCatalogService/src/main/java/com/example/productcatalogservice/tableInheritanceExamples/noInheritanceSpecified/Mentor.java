package com.example.productcatalogservice.tableInheritanceExamples.noInheritanceSpecified;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity(name = "ni_mentor")
public class Mentor extends User {
    private Long hours;
}
