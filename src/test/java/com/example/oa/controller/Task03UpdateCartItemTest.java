package com.example.oa.controller;

import com.example.oa.dto.CartItemRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Test class for Task 3: Update Cart Item
 * 
 * This test validates that the PUT /api/cart-items/{id} endpoint:
 * - Updates an existing cart item successfully
 * - Returns 200 OK status
 * - Validates that the cart item exists
 * - Validates that the new product exists
 * - Updates quantity and product information
 * - Recalculates subtotal correctly
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data/base-data.sql", 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cart-items.sql", 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup.sql", 
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class Task03UpdateCartItemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testUpdateCartItem_Success() throws Exception {
        CartItemRequest request = new CartItemRequest();
        request.setProductId(2L); // Change from Laptop to Mouse
        request.setQuantity(5);

        mockMvc.perform(put("/api/cart-items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.productId", is(2)))
                .andExpect(jsonPath("$.productName", is("Mouse")))
                .andExpect(jsonPath("$.quantity", is(5)))
                .andExpect(jsonPath("$.price", is(29.99)))
                .andExpect(jsonPath("$.subtotal", is(149.95))); // 5 * 29.99
    }

    @Test
    public void testUpdateCartItem_UpdateQuantityOnly() throws Exception {
        CartItemRequest request = new CartItemRequest();
        request.setProductId(1L); // Keep same product
        request.setQuantity(10);

        mockMvc.perform(put("/api/cart-items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.productName", is("Laptop")))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.subtotal", is(9999.90))); // 10 * 999.99
    }

    @Test
    public void testUpdateCartItem_NotFound() throws Exception {
        CartItemRequest request = new CartItemRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        mockMvc.perform(put("/api/cart-items/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Cart item")));
    }

    @Test
    public void testUpdateCartItem_ProductNotFound() throws Exception {
        CartItemRequest request = new CartItemRequest();
        request.setProductId(9999L); // Non-existent product
        request.setQuantity(2);

        mockMvc.perform(put("/api/cart-items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Product")));
    }

    @Test
    public void testUpdateCartItem_ValidationError_InvalidQuantity() throws Exception {
        CartItemRequest request = new CartItemRequest();
        request.setProductId(1L);
        request.setQuantity(0); // Invalid: zero quantity

        mockMvc.perform(put("/api/cart-items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }
}
