package com.example.oa.service;

import com.example.oa.dto.OrderRequest;
import com.example.oa.dto.OrderResponse;
import com.example.oa.dto.UpdateOrderStatusRequest;
import com.example.oa.entity.Order;
import com.example.oa.entity.OrderStatus;
import com.example.oa.exception.ResourceNotFoundException;
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

    // Task 5 - Create a new order
    public OrderResponse createOrder(OrderRequest request) {
        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setCustomerName(request.getCustomerName());
        order.setTotalAmount(request.getTotalAmount());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        
        Order saved = orderRepository.save(order);
        return mapToResponse(saved);
    }
    
    private OrderResponse mapToResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getCustomerName(),
                order.getOrderDate(),
                order.getStatus(),
                order.getTotalAmount()
        );
    }

    // Task 6 - Get an order by ID
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return mapToResponse(order);
    }

    // Task 7 - Get orders with pagination
    public Page<OrderResponse> getOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::mapToResponse);
    }

    // Task 8 - Filter orders by status
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable).map(this::mapToResponse);
    }

    // Task 9 - Filter orders by date range
    public Page<OrderResponse> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        if (startDate != null && endDate != null) {
            return orderRepository.findByOrderDateBetween(startDate, endDate, pageable).map(this::mapToResponse);
        } else if (startDate != null) {
            return orderRepository.findByOrderDateGreaterThanEqual(startDate, pageable).map(this::mapToResponse);
        } else if (endDate != null) {
            return orderRepository.findByOrderDateLessThanEqual(endDate, pageable).map(this::mapToResponse);
        }
        return orderRepository.findAll(pageable).map(this::mapToResponse);
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

    // TODO: Task 13 (BONUS) - Implement checkout: create order from cart items
    public OrderResponse checkout(Long customerId, String customerName) {
        throw new UnsupportedOperationException("Task 13: Implement checkout");
    }
}

