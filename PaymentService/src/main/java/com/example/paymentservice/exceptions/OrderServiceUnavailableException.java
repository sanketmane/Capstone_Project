package com.example.paymentservice.exceptions;

// thrown when OrderManagementService can't be reached to update order status
public class OrderServiceUnavailableException extends RuntimeException {
    public OrderServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
