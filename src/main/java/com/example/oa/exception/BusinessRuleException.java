package com.example.oa.exception;

/**
 * Exception thrown when a business rule is violated.
 * 
 * Results in HTTP 422 Unprocessable Entity response.
 * 
 * This exception is provided as part of the skeleton infrastructure.
 * Use for validation that goes beyond simple field validation (e.g., state transitions).
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
