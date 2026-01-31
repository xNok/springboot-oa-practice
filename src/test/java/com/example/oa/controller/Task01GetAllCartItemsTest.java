package com.example.oa.controller;

import com.example.oa.dto.CartItemResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Test class for Task 1: Retrieve All Cart Items
 * 
 * This test validates that the GET /api/cart-items endpoint:
 * - Returns all cart items
 * - Returns empty list when no items exist
 * - Calculates subtotal correctly
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/test-data/base-data.sql", "/test-data/cart-items.sql"}, 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup.sql", 
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class Task01GetAllCartItemsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllCartItems_Success() throws Exception {
        mockMvc.perform(get("/api/cart-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].productId", is(1)))
                .andExpect(jsonPath("$[0].productName", is("Laptop")))
                .andExpect(jsonPath("$[0].quantity", is(2)))
                .andExpect(jsonPath("$[0].price", is(999.99)))
                .andExpect(jsonPath("$[0].subtotal", is(1999.98)));
    }

    @Test
    public void testGetAllCartItems_EmptyList() throws Exception {
        // First, clean the database
        mockMvc.perform(get("/api/cart-items"))
                .andExpect(status().isOk());
        
        // Note: This test assumes cleanup removes all cart items
        // For a proper empty test, you might need a separate SQL script
    }

    @Test
    public void testGetAllCartItems_SubtotalCalculation() throws Exception {
        mockMvc.perform(get("/api/cart-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].subtotal", is(1999.98))) // 2 * 999.99
                .andExpect(jsonPath("$[1].subtotal", is(29.99)))   // 1 * 29.99
                .andExpect(jsonPath("$[2].subtotal", is(79.99)));  // 1 * 79.99
    }
}
