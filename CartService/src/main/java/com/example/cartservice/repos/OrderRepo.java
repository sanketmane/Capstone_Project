package com.example.cartservice.repos;

import com.example.cartservice.models.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepo extends MongoRepository<Order, String> {
    // MongoRepository<Order, String> means that this repository will manage Order entities with String type primary keys.
    List<Order> findByUserId(Long userId);
}
