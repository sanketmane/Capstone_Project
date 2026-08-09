package com.example.ordermanagementservice.controllers;

import com.example.ordermanagementservice.exceptions.CartServiceUnavailableException;
import com.example.ordermanagementservice.exceptions.EmptyCartException;
import com.example.ordermanagementservice.exceptions.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> handleNotFound(OrderNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({EmptyCartException.class, IllegalArgumentException.class})
    public ResponseEntity<String> handleBadRequest(RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CartServiceUnavailableException.class)
    public ResponseEntity<String> handleServiceUnavailable(CartServiceUnavailableException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
