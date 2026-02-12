package com.shikhar.intern_application.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handling for all controllers.
 * Intercepts specific exceptions and returns standardized JSON error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Catches validation errors triggered by @Valid (e.g., @NotBlank, @Size).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        
        log.warn("Validation failed for request: {}", ex.getBindingResult().getObjectName());
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error -> {
                    log.debug("Field '{}' error: {}", error.getField(), error.getDefaultMessage());
                    errors.put(error.getField(), error.getDefaultMessage());
                });

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Catches database constraint violations, like unique email conflicts.
     * This acts as a fallback if the service-level check is bypassed.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateEmail(
            DataIntegrityViolationException ex) {

        log.error("Database integrity violation: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "Email already exists");

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles generic RuntimeExceptions, including custom logic errors 
     * thrown from the Service layer.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(
            RuntimeException ex) {

        log.error("Runtime exception caught: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();

        // Logic to determine if the error is email-related for better UI feedback
        if (ex.getMessage() != null &&
            ex.getMessage().toLowerCase().contains("email")) {
            errors.put("email", ex.getMessage());
        } else {
            errors.put("general", ex.getMessage());
        }

        return ResponseEntity.badRequest().body(errors);
    }
}