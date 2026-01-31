package com.example.oa.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CartItem request DTO for creating or updating a cart item.
 * 
 * This DTO is provided as part of the skeleton.
 * Note: orderId is managed by the system and should not be set by clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    // Note: orderId is NOT included here - it's managed by the system
    // Cart items have orderId = null until checkout (Task 13)
}
