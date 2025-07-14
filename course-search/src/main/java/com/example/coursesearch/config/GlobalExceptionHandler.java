package com.example.coursesearch.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;
import java.util.Map;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(
                "timestamp", ZonedDateTime.now(),
                "status", 500,
                "error", "Internal Server Error",
                "message", ex.getMessage()
            ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleValidationException(Exception ex) {
        return ResponseEntity
            .badRequest()
            .body(Map.of(
                "timestamp", ZonedDateTime.now(),
                "status", 400,
                "error", "Bad Request",
                "message", ex.getMessage()
            ));
    }
}
