package com.example.cartservice.controllers;

import com.example.cartservice.exceptions.CartNotFoundException;
import com.example.cartservice.exceptions.InvalidQuantityException;
import com.example.cartservice.exceptions.ProductNotFoundException;
import com.example.cartservice.exceptions.ProductNotInCartException;
import com.example.cartservice.exceptions.ProductServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler({CartNotFoundException.class, ProductNotFoundException.class, ProductNotInCartException.class})
    public ResponseEntity<String> handleNotFound(RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InvalidQuantityException.class, IllegalArgumentException.class})
    public ResponseEntity<String> handleBadRequest(RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductServiceUnavailableException.class)
    public ResponseEntity<String> handleServiceUnavailable(ProductServiceUnavailableException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
