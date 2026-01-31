package com.example.oa.exception;

/**
 * Exception thrown when a requested resource is not found.
 * 
 * Results in HTTP 404 Not Found response.
 * 
 * This exception is provided as part of the skeleton infrastructure.
 * Candidates should throw this exception when entities are not found by ID.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " not found with id: " + id);
    }
}
