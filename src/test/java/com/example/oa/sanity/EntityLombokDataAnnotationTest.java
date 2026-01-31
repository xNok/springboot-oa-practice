package com.example.oa.sanity;

import com.example.oa.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sanity checks for Lombok @Data annotation on entities.
 * 
 * This test suite validates that:
 * - Getters and setters are correctly generated
 * - equals() and hashCode() work as expected
 * - toString() is functional
 * - No-arg and all-arg constructors work
 */
@DisplayName("Entity Lombok @Data Annotation Sanity Checks")
class EntityLombokDataAnnotationTest {

    @Test
    @DisplayName("Order entity should support all-arg constructor and getters")
    void testOrderAllArgConstructorAndGetters() {
        LocalDateTime now = LocalDateTime.now();
        Order order = new Order(1L, 100L, now, OrderStatus.CREATED, 99.99, "John Doe");
        
        assertEquals(1L, order.getId());
        assertEquals(100L, order.getCustomerId());
        assertEquals(now, order.getOrderDate());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(99.99, order.getTotalAmount());
        assertEquals("John Doe", order.getCustomerName());
    }

    @Test
    @DisplayName("Order entity should support no-arg constructor")
    void testOrderNoArgConstructor() {
        Order order = new Order();
        
        assertNull(order.getId());
        assertNull(order.getCustomerId());
        assertNull(order.getOrderDate());
        assertNull(order.getStatus());
        assertNull(order.getTotalAmount());
        assertNull(order.getCustomerName());
    }

    @Test
    @DisplayName("Order entity equals should work correctly")
    void testOrderEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        Order order1 = new Order(1L, 100L, now, OrderStatus.CREATED, 99.99, "John Doe");
        Order order2 = new Order(1L, 100L, now, OrderStatus.CREATED, 99.99, "John Doe");
        Order order3 = new Order(2L, 200L, now, OrderStatus.CREATED, 199.99, "Jane Doe");
        
        assertEquals(order1, order2);
        assertEquals(order1.hashCode(), order2.hashCode());
        assertNotEquals(order1, order3);
    }

    @Test
    @DisplayName("Order entity toString should contain field values")
    void testOrderToString() {
        Order order = new Order(1L, 100L, LocalDateTime.now(), OrderStatus.CREATED, 99.99, "John Doe");
        String orderString = order.toString();
        
        assertNotNull(orderString);
        assertTrue(orderString.contains("Order"));
        assertTrue(orderString.contains("1"));
    }

    @Test
    @DisplayName("CartItem entity should support all-arg constructor and getters")
    void testCartItemAllArgConstructorAndGetters() {
        CartItem cartItem = new CartItem(1L, null, 50L, 5, 19.99, "Laptop");
        
        assertEquals(1L, cartItem.getId());
        assertNull(cartItem.getOrderId());
        assertEquals(50L, cartItem.getProductId());
        assertEquals(5, cartItem.getQuantity());
        assertEquals(19.99, cartItem.getPrice());
        assertEquals("Laptop", cartItem.getProductName());
    }

    @Test
    @DisplayName("CartItem entity should support no-arg constructor")
    void testCartItemNoArgConstructor() {
        CartItem cartItem = new CartItem();
        
        assertNull(cartItem.getId());
        assertNull(cartItem.getOrderId());
        assertNull(cartItem.getProductId());
        assertNull(cartItem.getQuantity());
        assertNull(cartItem.getPrice());
        assertNull(cartItem.getProductName());
    }

    @Test
    @DisplayName("CartItem entity equals should work correctly")
    void testCartItemEqualsAndHashCode() {
        CartItem item1 = new CartItem(1L, null, 50L, 5, 19.99, "Laptop");
        CartItem item2 = new CartItem(1L, null, 50L, 5, 19.99, "Laptop");
        CartItem item3 = new CartItem(2L, null, 60L, 3, 29.99, "Mouse");
        
        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
        assertNotEquals(item1, item3);
    }

    @Test
    @DisplayName("Customer entity should support all-arg constructor and getters")
    void testCustomerAllArgConstructorAndGetters() {
        Customer customer = new Customer(1L, "John Doe", "john@example.com");
        
        assertEquals(1L, customer.getId());
        assertEquals("John Doe", customer.getName());
        assertEquals("john@example.com", customer.getEmail());
    }

    @Test
    @DisplayName("Customer entity should support no-arg constructor")
    void testCustomerNoArgConstructor() {
        Customer customer = new Customer();
        
        assertNull(customer.getId());
        assertNull(customer.getName());
        assertNull(customer.getEmail());
    }

    @Test
    @DisplayName("Product entity should support all-arg constructor and getters")
    void testProductAllArgConstructorAndGetters() {
        Product product = new Product(1L, "Laptop", 999.99, "High-performance laptop");
        
        assertEquals(1L, product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals(999.99, product.getPrice());
        assertEquals("High-performance laptop", product.getDescription());
    }

    @Test
    @DisplayName("Product entity should support no-arg constructor")
    void testProductNoArgConstructor() {
        Product product = new Product();
        
        assertNull(product.getId());
        assertNull(product.getName());
        assertNull(product.getPrice());
        assertNull(product.getDescription());
    }

    @Test
    @DisplayName("OrderStatus enum should have all required values")
    void testOrderStatusEnum() {
        assertNotNull(OrderStatus.CREATED);
        assertNotNull(OrderStatus.CONFIRMED);
        assertNotNull(OrderStatus.SHIPPED);
        assertNotNull(OrderStatus.DELIVERED);
        assertNotNull(OrderStatus.CANCELLED);
    }
}
