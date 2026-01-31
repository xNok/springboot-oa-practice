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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Test class for Task 2: Add Cart Item
 * 
 * This test validates that the POST /api/cart-items endpoint:
 * - Creates a new cart item successfully
 * - Returns 201 Created status
 * - Validates required fields
 * - Validates product existence
 * - Fetches product details (name, price)
 * - Calculates subtotal correctly
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data/base-data.sql", 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup.sql", 
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class Task02AddCartItemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAddCartItem_Success() throws Exception {
        CartItemRequest request = new CartItemRequest();
        request.setProductId(1L);
        request.setQuantity(3);

        mockMvc.perform(post("/api/cart-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.productName", is("Laptop")))
                .andExpect(jsonPath("$.quantity", is(3)))
                .andExpect(jsonPath("$.price", is(999.99)))
                .andExpect(jsonPath("$.subtotal", is(2999.97))); // 3 * 999.99
    }

    @Test
    public void testAddCartItem_ValidationError_MissingProductId() throws Exception {
        CartItemRequest request = new CartItemRequest();
        request.setQuantity(2);
        // Missing productId

        mockMvc.perform(post("/api/cart-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Validation failed")));
    }

    @Test
    public void testAddCartItem_ValidationError_InvalidQuantity() throws Exception {
        CartItemRequest request = new CartItemRequest();
        request.setProductId(1L);
        request.setQuantity(-1); // Invalid: negative quantity

        mockMvc.perform(post("/api/cart-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    public void testAddCartItem_ProductNotFound() throws Exception {
        CartItemRequest request = new CartItemRequest();
        request.setProductId(9999L); // Non-existent product
        request.setQuantity(2);

        mockMvc.perform(post("/api/cart-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Product")));
    }

    @Test
    public void testAddCartItem_FetchesProductDetails() throws Exception {
        CartItemRequest request = new CartItemRequest();
        request.setProductId(2L); // Mouse
        request.setQuantity(5);

        mockMvc.perform(post("/api/cart-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName", is("Mouse")))
                .andExpect(jsonPath("$.price", is(29.99)))
                .andExpect(jsonPath("$.subtotal", is(149.95))); // 5 * 29.99
    }
}
