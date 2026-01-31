package com.example.oa.dto;

import com.example.oa.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order request DTO for creating or updating an order.
 * 
 * This DTO is provided as part of the skeleton.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private String customerName;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private Double totalAmount;

    private OrderStatus status;
}
