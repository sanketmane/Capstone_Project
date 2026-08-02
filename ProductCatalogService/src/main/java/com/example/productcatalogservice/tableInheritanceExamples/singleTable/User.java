package com.example.productcatalogservice.tableInheritanceExamples.singleTable;

import jakarta.persistence.*;

import java.util.UUID;

@Entity(name = "st_user")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="user_type", discriminatorType = DiscriminatorType.INTEGER)
// For this usecase, "user_type" is being declared as discriminator column i.e.
// it will contain values like Ta, Mentor, Instructor which will be ENUM.
// discriminatorType = DiscriminatorType.INTEGER is just of denoting that ENUM i.e
// 1 => Ta, 2 => Mentor and so on...
public class User {
    @Id
    private UUID id;
    private String email;
}
