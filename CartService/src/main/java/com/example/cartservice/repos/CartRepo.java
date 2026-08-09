package com.example.cartservice.repos;

import com.example.cartservice.models.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CartRepo extends MongoRepository<Cart, String> {
    // MongoRepository<Cart, String> means that this repository will manage Cart entities with String type primary keys.
    Optional<Cart> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
