package com.example.productcatalogservice.tableInheritanceExamples.noInheritanceSpecified;

import jakarta.persistence.*;

import java.util.UUID;
// This is just an experimental package where we wanted to check
// what happens when we declare no inheritance.
// From what can be seen, it follows singleTable approach only table "ni_user"
// is created with fields from other classes and default discriminator field "dtype"
@Entity(name = "ni_user")
public class User {
    @Id
    private UUID id;
    private String email;
}
