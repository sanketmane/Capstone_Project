package com.example.usermanagementservice.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseModel {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // this annotation is used to auto-generate primary key value
    // based on db's auto-increment feature.
    // For this case, id would be auto-incremented.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Column(name = "last_updated_at")
    private Date lastUpdatedAt;

    private Status status;

    @PrePersist
    protected void setCreationTimestamps() {
        Date now = new Date();
        if (createdAt == null) {
            createdAt = now;
        }
        if (lastUpdatedAt == null) {
            lastUpdatedAt = now;
        }
    }

    @PreUpdate
    protected void setLastUpdatedTimestamp() {
        lastUpdatedAt = new Date();
    }
}
