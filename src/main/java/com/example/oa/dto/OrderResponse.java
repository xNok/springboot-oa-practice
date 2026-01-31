package com.example.oa.dto;

import com.example.oa.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Order response DTO.
 * 
 * Used when returning order information to clients.
 * This DTO is provided as part of the skeleton.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private Long customerId;
    private String customerName;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Double totalAmount;
}
