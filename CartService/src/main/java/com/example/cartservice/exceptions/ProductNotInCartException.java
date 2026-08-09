package com.example.cartservice.exceptions;

public class ProductNotInCartException extends RuntimeException {
    public ProductNotInCartException(String message) {
        super(message);
    }
}
