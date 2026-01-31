package com.example.oa.service;

import com.example.oa.dto.OrderRequest;
import com.example.oa.dto.OrderResponse;
import com.example.oa.dto.UpdateOrderStatusRequest;
import com.example.oa.entity.OrderStatus;
import com.example.oa.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Order service layer.
 * 
 * Candidates should implement the business logic in this service.
 * 
 * TODO: Implement CRUD, filtering, pagination, and state transition operations (Tasks 5-12)
 */
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // TODO: Task 5 - Implement method to create a new order
    public OrderResponse createOrder(OrderRequest request) {
        throw new UnsupportedOperationException("Task 5: Implement createOrder");
    }

    // TODO: Task 6 - Implement method to get an order by ID
    public OrderResponse getOrderById(Long id) {
        throw new UnsupportedOperationException("Task 6: Implement getOrderById");
    }

    // TODO: Task 7 - Implement method to get orders with pagination
    public Page<OrderResponse> getOrders(Pageable pageable) {
        throw new UnsupportedOperationException("Task 7: Implement getOrders with pagination");
    }

    // TODO: Task 8 - Implement method to filter orders by status
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        throw new UnsupportedOperationException("Task 8: Implement getOrdersByStatus");
    }

    // TODO: Task 9 - Implement method to filter orders by date range
    public Page<OrderResponse> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        throw new UnsupportedOperationException("Task 9: Implement getOrdersByDateRange");
    }

    // TODO: Task 10 - Implement method to update order status
    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        throw new UnsupportedOperationException("Task 10: Implement updateOrderStatus");
    }

    // TODO: Task 11 - Implement validation for state transitions
    // This is typically called within updateOrderStatus
    // Valid transitions: CREATED->CONFIRMED->SHIPPED->DELIVERED, or ->CANCELLED from most states
    // Invalid: DELIVERED cannot transition to any other state

    // TODO: Task 12 - Implement method to cancel an order
    public OrderResponse cancelOrder(Long id) {
        throw new UnsupportedOperationException("Task 12: Implement cancelOrder");
    }
}
