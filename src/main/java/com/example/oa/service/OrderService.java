package com.example.oa.service;

import com.example.oa.dto.OrderRequest;
import com.example.oa.dto.OrderResponse;
import com.example.oa.dto.UpdateOrderStatusRequest;
import com.example.oa.entity.CartItem;
import com.example.oa.entity.Order;
import com.example.oa.entity.OrderStatus;
import com.example.oa.exception.BusinessRuleException;
import com.example.oa.exception.ResourceNotFoundException;
import com.example.oa.repository.CartItemRepository;
import com.example.oa.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
    
    @Autowired
    private CartItemRepository cartItemRepository;

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

    // Task 10 - Update order status with validation
    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        
        OrderStatus newStatus = request.getStatus();
        
        // Check if trying to set same status
        if (order.getStatus() == newStatus) {
            throw new IllegalArgumentException("Invalid transition: order already has status " + newStatus);
        }
        
        // Special case: CANCELLED is a terminal state that shouldn't allow transitions (use IllegalArgumentException for consistency with same-status check)
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("Invalid transition from cancelled state");
        }
        
        // Task 11 - Validate state transition
        if (!isValidTransition(order.getStatus(), newStatus)) {
            throw new BusinessRuleException(
                "Invalid state transition from " + order.getStatus() + " to " + newStatus
            );
        }
        
        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);
        return mapToResponse(updated);
    }
    
    // Task 11 - Validate state transitions
    private boolean isValidTransition(OrderStatus from, OrderStatus to) {
        // DELIVERED and COMPLETED are final states
        if (from == OrderStatus.DELIVERED || from == OrderStatus.COMPLETED) {
            return false;
        }
        
        // Cannot transition to CREATED or PENDING (initial states)
        if (to == OrderStatus.CREATED || to == OrderStatus.PENDING) {
            return false;
        }
        
        // Define valid transitions
        switch (from) {
            case PENDING:
            case CREATED:
                return to == OrderStatus.CONFIRMED || to == OrderStatus.CANCELLED || to == OrderStatus.SHIPPED;
            case CONFIRMED:
                return to == OrderStatus.SHIPPED || to == OrderStatus.CANCELLED;
            case SHIPPED:
                return to == OrderStatus.DELIVERED || to == OrderStatus.COMPLETED || to == OrderStatus.CANCELLED;
            case CANCELLED:
                return false; // Cannot transition from CANCELLED
            default:
                return false;
        }
    }

    // Task 12 - Cancel an order
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        
        // Check if order can be cancelled
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a delivered order");
        }
        
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel order in status CANCELLED");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        Order updated = orderRepository.save(order);
        return mapToResponse(updated);
    }

    // Task 13 (BONUS) - Checkout: create order from cart items
    @Transactional
    public OrderResponse checkout(Long customerId, String customerName) {
        // Fetch all cart items not yet in an order
        List<CartItem> cartItems = cartItemRepository.findByOrderIdIsNull();
        
        // Validate cart is not empty
        if (cartItems.isEmpty()) {
            throw new BusinessRuleException("Cart is empty, cannot checkout");
        }
        
        // Calculate total amount
        double totalAmount = cartItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();
        
        // Round to 2 decimal places
        totalAmount = Math.round(totalAmount * 100.0) / 100.0;
        
        // Create new order
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setCustomerName(customerName);
        order.setTotalAmount(totalAmount);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        
        // Link all cart items to the order
        for (CartItem item : cartItems) {
            item.setOrderId(savedOrder.getId());
        }
        cartItemRepository.saveAll(cartItems);
        
        return mapToResponse(savedOrder);
    }
}

