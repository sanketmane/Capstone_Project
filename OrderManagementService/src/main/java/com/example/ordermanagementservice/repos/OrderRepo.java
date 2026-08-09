package com.example.ordermanagementservice.repos;

import com.example.ordermanagementservice.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// JpaRepository<Order, Long> means that this repository manages Order entities, 
// and the primary key of Order is of type Long.
public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}
