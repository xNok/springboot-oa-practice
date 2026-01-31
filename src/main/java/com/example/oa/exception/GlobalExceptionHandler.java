package com.example.oa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 * 
 * This handler is provided as part of the skeleton infrastructure.
 * It handles all common error scenarios and returns consistent ErrorResponse objects.
 * 
 * Candidates should use the provided custom exceptions in their implementation:
 * - ResourceNotFoundException -> 404 Not Found
 * - DuplicateResourceException -> 409 Conflict
 * - BusinessRuleException -> 422 Unprocessable Entity
 * - MethodArgumentNotValidException -> 400 Bad Request (validation errors)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException (404 Not Found)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException ex) {
        return new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    /**
     * Handles DuplicateResourceException (409 Conflict)
     */
    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateResource(DuplicateResourceException ex) {
        return new ErrorResponse(ex.getMessage(), HttpStatus.CONFLICT.value());
    }

    /**
     * Handles BusinessRuleException (422 Unprocessable Entity)
     */
    @ExceptionHandler(BusinessRuleException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleBusinessRuleViolation(BusinessRuleException ex) {
        return new ErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY.value());
    }

    /**
     * Handles validation errors from @Valid annotations (400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        
        return new ErrorResponse("Validation failed", HttpStatus.BAD_REQUEST.value(), errors);
    }

    /**
     * Handles all other unexpected exceptions (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        return new ErrorResponse(
            "An unexpected error occurred: " + ex.getMessage(), 
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
    }
}
