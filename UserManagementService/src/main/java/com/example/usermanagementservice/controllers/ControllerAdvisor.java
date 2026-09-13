package com.example.usermanagementservice.controllers;

import com.example.usermanagementservice.exceptions.InvalidTokenException;
import com.example.usermanagementservice.exceptions.PasswordMismatchException;
import com.example.usermanagementservice.exceptions.UnauthorizedException;
import com.example.usermanagementservice.exceptions.UserAlreadySignedUpException;
import com.example.usermanagementservice.exceptions.UserNotRegisteredException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// This annotation monitors exceptions mentioned below across the codebase and triggers the
// ExceptionHandler method handleExceptions()
// We will return Exception message in that case, just the HTTP status
@RestControllerAdvice
public class ControllerAdvisor {
    @ExceptionHandler({UserAlreadySignedUpException.class,
                       UserNotRegisteredException.class,
                       PasswordMismatchException.class,
                       InvalidTokenException.class})
    public ResponseEntity<String> handleExceptions(Exception exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
