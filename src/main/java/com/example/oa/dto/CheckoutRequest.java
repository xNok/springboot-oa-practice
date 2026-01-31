package com.example.oa.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Checkout request DTO for creating an order from cart items.
 * 
 * This DTO is provided as part of the skeleton.
 * Used in Task 13 - Checkout.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private String customerName;
}
