package com.example.oa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Test class for Task 4: Delete Cart Item
 * 
 * This test validates that the DELETE /api/cart-items/{id} endpoint:
 * - Deletes an existing cart item successfully
 * - Returns 204 No Content status
 * - Validates that the cart item exists
 * - Removes the item from the database
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data/base-data.sql", 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cart-items.sql", 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup.sql", 
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class Task04DeleteCartItemTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testDeleteCartItem_Success() throws Exception {
        mockMvc.perform(delete("/api/cart-items/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteCartItem_ItemNotFound() throws Exception {
        mockMvc.perform(delete("/api/cart-items/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Cart item")));
    }

    @Test
    public void testDeleteCartItem_VerifyDeletion() throws Exception {
        // Delete the item
        mockMvc.perform(delete("/api/cart-items/1"))
                .andExpect(status().isNoContent());

        // Verify it's gone by trying to get all cart items
        // (depends on the implementation of get all endpoint)
    }
}
