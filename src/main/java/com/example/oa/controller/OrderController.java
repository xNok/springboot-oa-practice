package com.example.oa.controller;

import com.example.oa.dto.OrderRequest;
import com.example.oa.dto.OrderResponse;
import com.example.oa.dto.UpdateOrderStatusRequest;
import com.example.oa.entity.OrderStatus;
import com.example.oa.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Order REST controller.
 * 
 * Candidates should implement the controller endpoints to handle HTTP requests.
 * Each endpoint should call the appropriate service method.
 * 
 * Error handling is already configured in GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Task 5: POST /api/orders
    // Request: OrderRequest (customerId, customerName, totalAmount)
    // Returns: OrderResponse
    // Status: 201 Created
    // Errors: 400 if validation fails


    // Task 6: GET /api/orders/{id}
    // Returns: OrderResponse
    // Status: 200 OK
    // Errors: 404 if order not found


    // Task 7: GET /api/orders?page=0&size=10&sort=orderDate,desc
    // Query params: page (default 0), size (default 10), sort (default id)
    // Returns: Page<OrderResponse>
    // Status: 200 OK


    // Task 8: GET /api/orders?status=CONFIRMED
    // Query params: status, page, size, sort
    // Returns: Page<OrderResponse>
    // Status: 200 OK


    // Task 9: GET /api/orders?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59
    // Query params: startDate, endDate, page, size, sort
    // Returns: Page<OrderResponse>
    // Status: 200 OK
    // Note: Dates should be in ISO format (yyyy-MM-ddTHH:mm:ss)


    // Task 10: PATCH /api/orders/{id}/status
    // Request: UpdateOrderStatusRequest (status)
    // Returns: OrderResponse
    // Status: 200 OK
    // Errors: 404 if order not found, 422 if invalid state transition


    // Task 11: State transition validation is implemented in the service layer
    // Valid transitions: CREATED->CONFIRMED->SHIPPED->DELIVERED, or ->CANCELLED from non-DELIVERED states
    // Invalid: DELIVERED cannot transition to any other state


    // Task 12: POST /api/orders/{id}/cancel
    // Returns: OrderResponse
    // Status: 200 OK
    // Errors: 404 if order not found, 422 if order cannot be cancelled (e.g., already DELIVERED)

}
