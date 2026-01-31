package com.example.oa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CartItem response DTO.
 * 
 * Used when returning cart item information to clients.
 * This DTO is provided as part of the skeleton.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private Double subtotal;  // quantity * price
}
