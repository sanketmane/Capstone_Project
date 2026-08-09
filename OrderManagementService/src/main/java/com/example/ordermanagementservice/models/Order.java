package com.example.ordermanagementservice.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders") // "order" is a reserved SQL keyword
public class Order extends BaseModel {
    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    // Prevents infinite recursion during JSON serialization. 
    // E.g. when serializing an Order, it will include its OrderItems, 
    // but those OrderItems won't include the Order again. 
    // But why can this happen? Because the OrderItem class 
    // has a @JsonBackReference on its order field, 
    // which tells Jackson to ignore that field during serialization.
    @JsonManagedReference 
    private List<OrderItem> items = new ArrayList<>();

    private Double totalAmount;

    @Embedded
    // DeliveryAddress.state would otherwise collide with BaseModel.state's "state" column
    @AttributeOverride(name = "state", column = @Column(name = "delivery_state"))
    private DeliveryAddress deliveryAddress;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public Order() {
        this.setCreatedAt(new Date());
        this.setLastUpdatedAt(new Date());
        this.setState(State.ACTIVE);
    }
}
