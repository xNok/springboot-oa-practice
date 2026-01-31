package com.example.oa.entity;

/**
 * Order status enumeration representing the lifecycle of an order.
 * 
 * Valid transitions:
 * - PENDING -> CONFIRMED -> SHIPPED -> DELIVERED/COMPLETED
 * - PENDING -> CANCELLED
 * - CONFIRMED -> CANCELLED
 * - SHIPPED -> CANCELLED (edge case)
 * 
 * Invalid transitions:
 * - DELIVERED/COMPLETED is final - cannot transition to any other state
 * - Cannot go backwards (e.g., SHIPPED -> CONFIRMED)
 * 
 * Note: PENDING is an alias for CREATED, COMPLETED is an alias for DELIVERED
 */
public enum OrderStatus {
    PENDING,     // Initial state when order is created
    CREATED,     // Alternative name for PENDING
    CONFIRMED,
    SHIPPED,
    DELIVERED,   // Final state - order successfully delivered
    COMPLETED,   // Alias for DELIVERED
    CANCELLED
}
