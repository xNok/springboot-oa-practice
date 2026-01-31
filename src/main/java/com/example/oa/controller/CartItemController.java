package com.example.oa.controller;

import com.example.oa.dto.CartItemRequest;
import com.example.oa.dto.CartItemResponse;
import com.example.oa.service.CartItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CartItem REST controller.
 * 
 * Candidates should implement the controller endpoints to handle HTTP requests.
 * Each endpoint should call the appropriate service method.
 * 
 * Error handling is already configured in GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/cart/items")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    // Task 1: GET /api/cart/items
    // Returns: List<CartItemResponse>
    // Status: 200 OK (returns empty list if no items)
    @GetMapping
    public List<CartItemResponse> getAllCartItems() {
        return cartItemService.getAllCartItems();
    }


    // Task 2: POST /api/cart/items
    // Request: CartItemRequest (productId, quantity)
    // Returns: CartItemResponse
    // Status: 201 Created
    // Errors: 404 if product not found, 400 if validation fails


    // Task 3: PUT /api/cart/items/{id}
    // Request: CartItemRequest (productId, quantity)
    // Returns: CartItemResponse
    // Status: 200 OK
    // Errors: 404 if cart item not found, 404 if product not found, 400 if validation fails


    // Task 4: DELETE /api/cart/items/{id}
    // Status: 204 No Content
    // Errors: 404 if cart item not found

}

