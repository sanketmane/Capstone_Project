package com.example.ordermanagementservice.exceptions;

public class CartServiceUnavailableException extends RuntimeException {
    // Throwable cause is the underlying exception 
    // that caused this exception to be thrown.
    public CartServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
