package com.example.oa.entity;

/**
 * Order status enumeration representing the lifecycle of an order.
 * 
 * Valid transitions:
 * - CREATED -> CONFIRMED -> SHIPPED -> DELIVERED
 * - CREATED -> CANCELLED
 * - CONFIRMED -> CANCELLED
 * - SHIPPED -> CANCELLED (edge case)
 * 
 * Invalid transitions:
 * - DELIVERED cannot transition to any other state
 * - Cannot go backwards (e.g., SHIPPED -> CONFIRMED)
 */
public enum OrderStatus {
    CREATED,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
