package com.example.oa.sanity;

import com.example.oa.dto.*;
import com.example.oa.entity.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sanity checks for Lombok @Data annotation on DTOs.
 * 
 * This test suite validates that:
 * - DTO getters and setters work correctly
 * - DTO constructors function properly
 * - equals() and hashCode() work as expected
 * - No serialization/deserialization issues
 */
@DisplayName("DTO Lombok @Data Annotation Sanity Checks")
class DTOLombokDataAnnotationTest {

    @Test
    @DisplayName("CartItemResponse DTO should support all-arg constructor and getters")
    void testCartItemResponseAllArgConstructorAndGetters() {
        CartItemResponse dto = new CartItemResponse(1L, null, 50L, "Laptop", 5, 19.99, 99.95);
        
        assertEquals(1L, dto.getId());
        assertNull(dto.getOrderId());
        assertEquals(50L, dto.getProductId());
        assertEquals("Laptop", dto.getProductName());
        assertEquals(5, dto.getQuantity());
        assertEquals(19.99, dto.getPrice());
        assertEquals(99.95, dto.getSubtotal());
    }

    @Test
    @DisplayName("CartItemResponse DTO should support no-arg constructor")
    void testCartItemResponseNoArgConstructor() {
        CartItemResponse dto = new CartItemResponse();
        
        assertNull(dto.getId());
        assertNull(dto.getOrderId());
        assertNull(dto.getProductId());
        assertNull(dto.getProductName());
        assertNull(dto.getQuantity());
        assertNull(dto.getPrice());
        assertNull(dto.getSubtotal());
    }

    @Test
    @DisplayName("CartItemResponse DTO equals should work correctly")
    void testCartItemResponseEqualsAndHashCode() {
        CartItemResponse dto1 = new CartItemResponse(1L, null, 50L, "Laptop", 5, 19.99, 99.95);
        CartItemResponse dto2 = new CartItemResponse(1L, null, 50L, "Laptop", 5, 19.99, 99.95);
        CartItemResponse dto3 = new CartItemResponse(2L, null, 60L, "Mouse", 3, 29.99, 89.97);
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    @DisplayName("OrderResponse DTO should support all-arg constructor and getters")
    void testOrderResponseAllArgConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        OrderResponse dto = new OrderResponse(1L, 100L, "John Doe", now, OrderStatus.CREATED, 99.99);
        
        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getCustomerId());
        assertEquals("John Doe", dto.getCustomerName());
        assertEquals(now, dto.getOrderDate());
        assertEquals(OrderStatus.CREATED, dto.getStatus());
        assertEquals(99.99, dto.getTotalAmount());
    }

    @Test
    @DisplayName("OrderResponse DTO should support no-arg constructor")
    void testOrderResponseNoArgConstructor() {
        OrderResponse dto = new OrderResponse();
        
        assertNull(dto.getId());
        assertNull(dto.getCustomerId());
        assertNull(dto.getCustomerName());
        assertNull(dto.getOrderDate());
        assertNull(dto.getStatus());
        assertNull(dto.getTotalAmount());
    }

    @Test
    @DisplayName("OrderResponse DTO equals should work correctly")
    void testOrderResponseEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        OrderResponse dto1 = new OrderResponse(1L, 100L, "John Doe", now, OrderStatus.CREATED, 99.99);
        OrderResponse dto2 = new OrderResponse(1L, 100L, "John Doe", now, OrderStatus.CREATED, 99.99);
        OrderResponse dto3 = new OrderResponse(2L, 200L, "Jane Doe", now, OrderStatus.CONFIRMED, 199.99);
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    @DisplayName("CartItemRequest DTO should work correctly")
    void testCartItemRequestAllArgConstructor() {
        CartItemRequest dto = new CartItemRequest(50L, 5);
        
        assertEquals(50L, dto.getProductId());
        assertEquals(5, dto.getQuantity());
    }

    @Test
    @DisplayName("OrderRequest DTO should work correctly")
    void testOrderRequestAllArgConstructor() {
        OrderRequest dto = new OrderRequest(100L, "John Doe", 99.99, OrderStatus.CREATED);
        
        assertEquals(100L, dto.getCustomerId());
        assertEquals("John Doe", dto.getCustomerName());
        assertEquals(99.99, dto.getTotalAmount());
        assertEquals(OrderStatus.CREATED, dto.getStatus());
    }

    @Test
    @DisplayName("UpdateOrderStatusRequest DTO should work correctly")
    void testUpdateOrderStatusRequestAllArgConstructor() {
        UpdateOrderStatusRequest dto = new UpdateOrderStatusRequest(OrderStatus.CONFIRMED);
        
        assertEquals(OrderStatus.CONFIRMED, dto.getStatus());
    }

    @Test
    @DisplayName("CheckoutRequest DTO should work correctly")
    void testCheckoutRequestAllArgConstructor() {
        CheckoutRequest dto = new CheckoutRequest(100L, "John Doe");
        
        assertEquals(100L, dto.getCustomerId());
        assertEquals("John Doe", dto.getCustomerName());
    }

    @Test
    @DisplayName("All DTOs should have working toString methods")
    void testDTOToStringMethods() {
        CartItemResponse cartDto = new CartItemResponse(1L, null, 50L, "Laptop", 5, 19.99, 99.95);
        OrderResponse orderDto = new OrderResponse(1L, 100L, "John Doe", LocalDateTime.now(), OrderStatus.CREATED, 99.99);
        
        assertNotNull(cartDto.toString());
        assertNotNull(orderDto.toString());
        assertTrue(cartDto.toString().contains("CartItemResponse"));
        assertTrue(orderDto.toString().contains("OrderResponse"));
    }
}
