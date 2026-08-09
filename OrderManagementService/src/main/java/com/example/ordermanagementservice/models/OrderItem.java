package com.example.ordermanagementservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class OrderItem extends BaseModel {
    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;

    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private Double subtotal;

    public OrderItem() {
        this.setCreatedAt(new java.util.Date());
        this.setLastUpdatedAt(new java.util.Date());
        this.setState(State.ACTIVE);
    }
}
