package com.example.oa.exception;

/**
 * Exception thrown when attempting to create a duplicate resource or violate unique constraints.
 * 
 * Results in HTTP 409 Conflict response.
 * 
 * This exception is provided as part of the skeleton infrastructure.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
