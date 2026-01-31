package com.example.oa.controller;

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
 * Test class for Task 6: Get Order by ID
 * 
 * This test validates that the GET /api/orders/{id} endpoint:
 * - Returns order by ID successfully
 * - Returns 404 when order not found
 * - Returns all order fields correctly
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/test-data/base-data.sql", "/test-data/orders.sql"}, 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup.sql", 
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class Task06GetOrderByIdTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetOrderById_Success() throws Exception {
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.customerId", is(1)))
                .andExpect(jsonPath("$.customerName", is("John Doe")))
                .andExpect(jsonPath("$.status", is("CREATED")))
                .andExpect(jsonPath("$.totalAmount", is(2029.97)))
                .andExpect(jsonPath("$.orderDate", notNullValue()));
    }

    @Test
    public void testGetOrderById_NotFound() throws Exception {
        mockMvc.perform(get("/api/orders/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Order")));
    }

    @Test
    public void testGetOrderById_DifferentStatuses() throws Exception {
        // Test CONFIRMED status
        mockMvc.perform(get("/api/orders/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CONFIRMED")));

        // Test SHIPPED status
        mockMvc.perform(get("/api/orders/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SHIPPED")));

        // Test DELIVERED status
        mockMvc.perform(get("/api/orders/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("DELIVERED")));
    }
}
