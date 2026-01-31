package com.example.oa.sanity;

import com.example.oa.dto.CartItemResponse;
import com.example.oa.dto.OrderResponse;
import com.example.oa.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sanity checks for JSON serialization/deserialization.
 * 
 * This test suite validates that:
 * - Entities can be serialized to JSON without issues
 * - DTOs can be serialized to JSON without issues
 * - No circular references or serialization loops
 * - LocalDateTime serialization works correctly
 */
@DisplayName("Entity and DTO JSON Serialization Sanity Checks")
class SerializationSanityTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @DisplayName("Order entity should serialize to JSON without errors")
    void testOrderEntitySerialization() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Order order = new Order(1L, 100L, now, OrderStatus.CREATED, 99.99, "John Doe");
        
        String jsonString = objectMapper.writeValueAsString(order);
        
        assertNotNull(jsonString);
        assertTrue(jsonString.contains("\"id\":1"));
        assertTrue(jsonString.contains("\"customerId\":100"));
        assertTrue(jsonString.contains("\"totalAmount\":99.99"));
        assertTrue(jsonString.contains("\"status\":\"CREATED\""));
    }

    @Test
    @DisplayName("Order entity should deserialize from JSON without errors")
    void testOrderEntityDeserialization() throws Exception {
        String json = "{\"id\":1,\"customerId\":100,\"orderDate\":\"2023-01-15T10:30:00\",\"status\":\"CREATED\",\"totalAmount\":99.99,\"customerName\":\"John Doe\"}";
        
        Order order = objectMapper.readValue(json, Order.class);
        
        assertNotNull(order);
        assertEquals(1L, order.getId());
        assertEquals(100L, order.getCustomerId());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(99.99, order.getTotalAmount());
        assertEquals("John Doe", order.getCustomerName());
    }

    @Test
    @DisplayName("CartItem entity should serialize to JSON without errors")
    void testCartItemEntitySerialization() throws Exception {
        CartItem cartItem = new CartItem(1L, null, 50L, 5, 19.99, "Laptop");
        
        String jsonString = objectMapper.writeValueAsString(cartItem);
        
        assertNotNull(jsonString);
        assertTrue(jsonString.contains("\"id\":1"));
        assertTrue(jsonString.contains("\"productId\":50"));
        assertTrue(jsonString.contains("\"quantity\":5"));
        assertTrue(jsonString.contains("\"price\":19.99"));
    }

    @Test
    @DisplayName("CartItem entity should deserialize from JSON without errors")
    void testCartItemEntityDeserialization() throws Exception {
        String json = "{\"id\":1,\"orderId\":null,\"productId\":50,\"quantity\":5,\"price\":19.99,\"productName\":\"Laptop\"}";
        
        CartItem cartItem = objectMapper.readValue(json, CartItem.class);
        
        assertNotNull(cartItem);
        assertEquals(1L, cartItem.getId());
        assertNull(cartItem.getOrderId());
        assertEquals(50L, cartItem.getProductId());
        assertEquals(5, cartItem.getQuantity());
        assertEquals(19.99, cartItem.getPrice());
    }

    @Test
    @DisplayName("Customer entity should serialize to JSON without errors")
    void testCustomerEntitySerialization() throws Exception {
        Customer customer = new Customer(1L, "John Doe", "john@example.com");
        
        String jsonString = objectMapper.writeValueAsString(customer);
        
        assertNotNull(jsonString);
        assertTrue(jsonString.contains("\"id\":1"));
        assertTrue(jsonString.contains("\"name\":\"John Doe\""));
        assertTrue(jsonString.contains("\"email\":\"john@example.com\""));
    }

    @Test
    @DisplayName("Product entity should serialize to JSON without errors")
    void testProductEntitySerialization() throws Exception {
        Product product = new Product(1L, "Laptop", 999.99, "High-performance laptop");
        
        String jsonString = objectMapper.writeValueAsString(product);
        
        assertNotNull(jsonString);
        assertTrue(jsonString.contains("\"id\":1"));
        assertTrue(jsonString.contains("\"name\":\"Laptop\""));
        assertTrue(jsonString.contains("\"price\":999.99"));
    }

    @Test
    @DisplayName("OrderResponse DTO should serialize to JSON without errors")
    void testOrderResponseDTOSerialization() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        OrderResponse dto = new OrderResponse(1L, 100L, "John Doe", now, OrderStatus.CREATED, 99.99);
        
        String jsonString = objectMapper.writeValueAsString(dto);
        
        assertNotNull(jsonString);
        assertTrue(jsonString.contains("\"id\":1"));
        assertTrue(jsonString.contains("\"customerId\":100"));
        assertTrue(jsonString.contains("\"status\":\"CREATED\""));
    }

    @Test
    @DisplayName("OrderResponse DTO should deserialize from JSON without errors")
    void testOrderResponseDTODeserialization() throws Exception {
        String json = "{\"id\":1,\"customerId\":100,\"customerName\":\"John Doe\",\"orderDate\":\"2023-01-15T10:30:00\",\"status\":\"CREATED\",\"totalAmount\":99.99}";
        
        OrderResponse dto = objectMapper.readValue(json, OrderResponse.class);
        
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getCustomerId());
        assertEquals("John Doe", dto.getCustomerName());
        assertEquals(OrderStatus.CREATED, dto.getStatus());
        assertEquals(99.99, dto.getTotalAmount());
    }

    @Test
    @DisplayName("CartItemResponse DTO should serialize to JSON without errors")
    void testCartItemResponseDTOSerialization() throws Exception {
        CartItemResponse dto = new CartItemResponse(1L, null, 50L, "Laptop", 5, 19.99, 99.95);
        
        String jsonString = objectMapper.writeValueAsString(dto);
        
        assertNotNull(jsonString);
        assertTrue(jsonString.contains("\"id\":1"));
        assertTrue(jsonString.contains("\"productId\":50"));
        assertTrue(jsonString.contains("\"subtotal\":99.95"));
    }

    @Test
    @DisplayName("CartItemResponse DTO should deserialize from JSON without errors")
    void testCartItemResponseDTODeserialization() throws Exception {
        String json = "{\"id\":1,\"orderId\":null,\"productId\":50,\"productName\":\"Laptop\",\"quantity\":5,\"price\":19.99,\"subtotal\":99.95}";
        
        CartItemResponse dto = objectMapper.readValue(json, CartItemResponse.class);
        
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertNull(dto.getOrderId());
        assertEquals(50L, dto.getProductId());
        assertEquals("Laptop", dto.getProductName());
        assertEquals(99.95, dto.getSubtotal());
    }

    @Test
    @DisplayName("LocalDateTime should serialize correctly in entities")
    void testLocalDateTimeSerialization() throws Exception {
        LocalDateTime testDateTime = LocalDateTime.of(2023, 1, 15, 10, 30, 45);
        Order order = new Order(1L, 100L, testDateTime, OrderStatus.CREATED, 99.99, "John Doe");
        
        String jsonString = objectMapper.writeValueAsString(order);
        Order deserializedOrder = objectMapper.readValue(jsonString, Order.class);
        
        assertEquals(testDateTime, deserializedOrder.getOrderDate());
    }

    @Test
    @DisplayName("OrderStatus enum should serialize correctly in JSON")
    void testOrderStatusEnumSerialization() throws Exception {
        Order order = new Order(1L, 100L, LocalDateTime.now(), OrderStatus.CONFIRMED, 99.99, "John Doe");
        
        String jsonString = objectMapper.writeValueAsString(order);
        
        assertTrue(jsonString.contains("\"status\":\"CONFIRMED\""));
    }

    @Test
    @DisplayName("Round-trip serialization should preserve all Order data")
    void testOrderRoundTripSerialization() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        Order original = new Order(1L, 100L, now, OrderStatus.CONFIRMED, 99.99, "John Doe");
        
        String jsonString = objectMapper.writeValueAsString(original);
        Order deserialized = objectMapper.readValue(jsonString, Order.class);
        
        assertEquals(original, deserialized);
    }

    @Test
    @DisplayName("Round-trip serialization should preserve all CartItem data")
    void testCartItemRoundTripSerialization() throws Exception {
        CartItem original = new CartItem(1L, null, 50L, 5, 19.99, "Laptop");
        
        String jsonString = objectMapper.writeValueAsString(original);
        CartItem deserialized = objectMapper.readValue(jsonString, CartItem.class);
        
        assertEquals(original, deserialized);
    }

    @Test
    @DisplayName("Round-trip serialization should preserve all OrderResponse DTO data")
    void testOrderResponseRoundTripSerialization() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        OrderResponse original = new OrderResponse(1L, 100L, "John Doe", now, OrderStatus.CREATED, 99.99);
        
        String jsonString = objectMapper.writeValueAsString(original);
        OrderResponse deserialized = objectMapper.readValue(jsonString, OrderResponse.class);
        
        assertEquals(original, deserialized);
    }
}
