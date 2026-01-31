package com.example.oa.entity;

/**
 * Order status enumeration representing the lifecycle of an order.
 * 
 * Valid transitions:
 * - PENDING -> CONFIRMED -> SHIPPED -> DELIVERED
 * - PENDING -> CANCELLED
 * - CONFIRMED -> CANCELLED
 * - SHIPPED -> CANCELLED (edge case)
 * 
 * Invalid transitions:
 * - DELIVERED cannot transition to any other state
 * - Cannot go backwards (e.g., SHIPPED -> CONFIRMED)
 * 
 * Note: PENDING is an alias/synonym for CREATED in some contexts
 */
public enum OrderStatus {
    PENDING,     // Initial state when order is created (alias for CREATED)
    CREATED,     // Alternative name for initial state
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
