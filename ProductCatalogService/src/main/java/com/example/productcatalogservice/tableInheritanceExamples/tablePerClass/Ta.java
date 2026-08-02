package com.example.productcatalogservice.tableInheritanceExamples.tablePerClass;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity(name = "tpc_ta")
public class Ta extends User {
    private Double ratings;
}
