package com.example.oa.controller;

import com.example.oa.dto.CheckoutRequest;
import com.example.oa.dto.OrderResponse;
import com.example.oa.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Cart REST controller for cart-level operations.
 * 
 * Candidates should implement cart-level endpoints here.
 * 
 * Error handling is already configured in GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private OrderService orderService;

    // Task 13 (BONUS): POST /api/cart/checkout
    // Request: CheckoutRequest (customerId, customerName)
    // Returns: OrderResponse
    // Status: 201 Created
    // Business Logic:
    //   1. Fetch all cart items where orderId is null
    //   2. Validate cart is not empty (throw BusinessRuleException if empty)
    //   3. Calculate total from cart items (sum of subtotals)
    //   4. Create order with calculated total
    //   5. Link all cart items to the order (set orderId)
    //   6. Return created order
    // Errors: 400 if validation fails, 422 if cart is empty

}
