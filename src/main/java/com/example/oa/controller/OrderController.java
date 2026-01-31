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
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }


    // Task 6: GET /api/orders/{id}
    // Returns: OrderResponse
    // Status: 200 OK
    // Errors: 404 if order not found
    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }


    // Task 7: GET /api/orders?page=0&size=10&sort=orderDate,desc
    // Query params: page (default 0), size (default 10), sort (default id)
    // Returns: Page<OrderResponse>
    // Status: 200 OK
    // Task 8: Also supports ?status=CONFIRMED
    // Task 9: Also supports ?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59
    @GetMapping
    public Page<OrderResponse> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {
        
        // Parse sort parameter
        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc") 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        
        // Task 9 - Filter by date range (single or both dates)
        if (startDate != null || endDate != null) {
            return orderService.getOrdersByDateRange(startDate, endDate, pageable);
        }
        
        // Task 8 - Filter by status
        if (status != null) {
            return orderService.getOrdersByStatus(status, pageable);
        }
        
        // Task 7 - Pagination only
        return orderService.getOrders(pageable);
    }


    // Task 10: PATCH /api/orders/{id}/status
    // Request: UpdateOrderStatusRequest (status)
    // Returns: OrderResponse
    // Status: 200 OK
    // Errors: 404 if order not found, 422 if invalid state transition
    @PatchMapping("/{id}/status")
    public OrderResponse updateOrderStatus(@PathVariable Long id, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateOrderStatus(id, request);
    }


    // Task 11: State transition validation is implemented in the service layer
    // Valid transitions: CREATED->CONFIRMED->SHIPPED->DELIVERED, or ->CANCELLED from non-DELIVERED states
    // Invalid: DELIVERED cannot transition to any other state


    // Task 12: POST /api/orders/{id}/cancel
    // Returns: OrderResponse
    // Status: 200 OK
    // Errors: 404 if order not found, 422 if order cannot be cancelled (e.g., already DELIVERED)
    @PostMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

}
