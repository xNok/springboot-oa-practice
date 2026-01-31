package com.example.oa.sanity;

import com.example.oa.dto.CartItemResponse;
import com.example.oa.dto.OrderResponse;
import com.example.oa.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sanity checks for simple entity-to-DTO mapping.
 * 
 * This test suite validates that:
 * - Basic field mapping from entities to DTOs works
 * - No complex mapping logic required (straightforward field assignment)
 * - DTOs can be constructed from entity data without tedious conversion
 * 
 * NOTE: These tests demonstrate that simple mapping is sufficient.
 * The intent is to validate that candidates don't need to write complex
 * mapping logic or use mapping libraries for this practice exercise.
 */
@DisplayName("Entity-to-DTO Mapping Sanity Checks")
class EntityToDTOMappingSanityTest {

    @Test
    @DisplayName("Order entity fields should map to OrderResponse DTO fields without issues")
    void testOrderToOrderResponseMapping() {
        LocalDateTime now = LocalDateTime.now();
        Order order = new Order(1L, 100L, now, OrderStatus.CREATED, 99.99, "John Doe");
        
        // Simple direct field assignment
        OrderResponse dto = new OrderResponse(
            order.getId(),
            order.getCustomerId(),
            order.getCustomerName(),
            order.getOrderDate(),
            order.getStatus(),
            order.getTotalAmount()
        );
        
        assertEquals(order.getId(), dto.getId());
        assertEquals(order.getCustomerId(), dto.getCustomerId());
        assertEquals(order.getCustomerName(), dto.getCustomerName());
        assertEquals(order.getOrderDate(), dto.getOrderDate());
        assertEquals(order.getStatus(), dto.getStatus());
        assertEquals(order.getTotalAmount(), dto.getTotalAmount());
    }

    @Test
    @DisplayName("CartItem entity fields should map to CartItemResponse DTO fields without issues")
    void testCartItemToCartItemResponseMapping() {
        CartItem cartItem = new CartItem(1L, null, 50L, 5, 19.99, "Laptop");
        
        // Simple direct field assignment with computed field
        CartItemResponse dto = new CartItemResponse(
            cartItem.getId(),
            cartItem.getOrderId(),
            cartItem.getProductId(),
            cartItem.getProductName(),
            cartItem.getQuantity(),
            cartItem.getPrice(),
            cartItem.getQuantity() * cartItem.getPrice()  // Simple calculation
        );
        
        assertEquals(cartItem.getId(), dto.getId());
        assertEquals(cartItem.getOrderId(), dto.getOrderId());
        assertEquals(cartItem.getProductId(), dto.getProductId());
        assertEquals(cartItem.getProductName(), dto.getProductName());
        assertEquals(cartItem.getQuantity(), dto.getQuantity());
        assertEquals(cartItem.getPrice(), dto.getPrice());
        assertEquals(99.95, dto.getSubtotal(), 0.01);
    }

    @Test
    @DisplayName("All Order entity fields align with OrderResponse DTO fields")
    void testOrderFieldAlignment() {
        LocalDateTime now = LocalDateTime.now();
        Order order = new Order(1L, 100L, now, OrderStatus.CONFIRMED, 199.99, "Jane Doe");
        
        // Verify no field name mismatches
        OrderResponse dto = new OrderResponse();
        dto.setId(order.getId());
        dto.setCustomerId(order.getCustomerId());
        dto.setCustomerName(order.getCustomerName());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        
        assertAll(
            () -> assertEquals(order.getId(), dto.getId()),
            () -> assertEquals(order.getCustomerId(), dto.getCustomerId()),
            () -> assertEquals(order.getCustomerName(), dto.getCustomerName()),
            () -> assertEquals(order.getOrderDate(), dto.getOrderDate()),
            () -> assertEquals(order.getStatus(), dto.getStatus()),
            () -> assertEquals(order.getTotalAmount(), dto.getTotalAmount())
        );
    }

    @Test
    @DisplayName("All CartItem entity fields align with CartItemResponse DTO fields")
    void testCartItemFieldAlignment() {
        CartItem cartItem = new CartItem(1L, 10L, 50L, 5, 19.99, "Laptop");
        
        // Verify no field name mismatches
        CartItemResponse dto = new CartItemResponse();
        dto.setId(cartItem.getId());
        dto.setOrderId(cartItem.getOrderId());
        dto.setProductId(cartItem.getProductId());
        dto.setProductName(cartItem.getProductName());
        dto.setQuantity(cartItem.getQuantity());
        dto.setPrice(cartItem.getPrice());
        dto.setSubtotal(cartItem.getQuantity() * cartItem.getPrice());
        
        assertAll(
            () -> assertEquals(cartItem.getId(), dto.getId()),
            () -> assertEquals(cartItem.getOrderId(), dto.getOrderId()),
            () -> assertEquals(cartItem.getProductId(), dto.getProductId()),
            () -> assertEquals(cartItem.getProductName(), dto.getProductName()),
            () -> assertEquals(cartItem.getQuantity(), dto.getQuantity()),
            () -> assertEquals(cartItem.getPrice(), dto.getPrice())
        );
    }

    @Test
    @DisplayName("Multiple CartItems should map to CartItemResponse DTOs consistently")
    void testMultipleCartItemsMapping() {
        CartItem item1 = new CartItem(1L, null, 50L, 5, 19.99, "Laptop");
        CartItem item2 = new CartItem(2L, null, 51L, 3, 9.99, "Mouse");
        CartItem item3 = new CartItem(3L, null, 52L, 2, 99.99, "Monitor");
        
        CartItemResponse dto1 = mapCartItemToDTO(item1);
        CartItemResponse dto2 = mapCartItemToDTO(item2);
        CartItemResponse dto3 = mapCartItemToDTO(item3);
        
        assertEquals(99.95, dto1.getSubtotal(), 0.01);
        assertEquals(29.97, dto2.getSubtotal(), 0.01);
        assertEquals(199.98, dto3.getSubtotal(), 0.01);
    }

    @Test
    @DisplayName("Multiple Orders should map to OrderResponse DTOs consistently")
    void testMultipleOrdersMapping() {
        LocalDateTime time1 = LocalDateTime.of(2023, 1, 15, 10, 30);
        LocalDateTime time2 = LocalDateTime.of(2023, 1, 16, 14, 45);
        
        Order order1 = new Order(1L, 100L, time1, OrderStatus.CREATED, 99.99, "John Doe");
        Order order2 = new Order(2L, 101L, time2, OrderStatus.CONFIRMED, 199.99, "Jane Doe");
        
        OrderResponse dto1 = mapOrderToDTO(order1);
        OrderResponse dto2 = mapOrderToDTO(order2);
        
        assertEquals("John Doe", dto1.getCustomerName());
        assertEquals("Jane Doe", dto2.getCustomerName());
        assertEquals(OrderStatus.CREATED, dto1.getStatus());
        assertEquals(OrderStatus.CONFIRMED, dto2.getStatus());
    }

    @Test
    @DisplayName("Null and empty fields should be handled gracefully in mapping")
    void testMappingWithNullFields() {
        CartItem cartItem = new CartItem(1L, null, 50L, 5, 19.99, null);
        
        CartItemResponse dto = new CartItemResponse(
            cartItem.getId(),
            cartItem.getOrderId(),
            cartItem.getProductId(),
            cartItem.getProductName(),
            cartItem.getQuantity(),
            cartItem.getPrice(),
            cartItem.getQuantity() * cartItem.getPrice()
        );
        
        assertNull(dto.getOrderId());
        assertNull(dto.getProductName());
        assertEquals(99.95, dto.getSubtotal(), 0.01);
    }

    @Test
    @DisplayName("Mapping should preserve data types correctly")
    void testMappingDataTypePreservation() {
        Order order = new Order(1L, 100L, LocalDateTime.now(), OrderStatus.CONFIRMED, 99.99, "John Doe");
        OrderResponse dto = mapOrderToDTO(order);
        
        assertTrue(dto.getId() instanceof Long);
        assertTrue(dto.getCustomerId() instanceof Long);
        assertTrue(dto.getTotalAmount() instanceof Double);
        assertNotNull(dto.getOrderDate());
        assertTrue(dto.getStatus() instanceof OrderStatus);
    }

    // Helper methods to demonstrate simple mapping
    private OrderResponse mapOrderToDTO(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getCustomerId(),
            order.getCustomerName(),
            order.getOrderDate(),
            order.getStatus(),
            order.getTotalAmount()
        );
    }

    private CartItemResponse mapCartItemToDTO(CartItem cartItem) {
        return new CartItemResponse(
            cartItem.getId(),
            cartItem.getOrderId(),
            cartItem.getProductId(),
            cartItem.getProductName(),
            cartItem.getQuantity(),
            cartItem.getPrice(),
            cartItem.getQuantity() * cartItem.getPrice()
        );
    }
}
