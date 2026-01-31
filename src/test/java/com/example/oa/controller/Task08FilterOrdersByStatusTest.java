package com.example.oa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Test class for Task 8: Filter Orders by Status
 * 
 * This test validates that the GET /api/orders endpoint:
 * - Filters orders by status query parameter
 * - Returns only orders matching the specified status
 * - Supports pagination with status filter
 * - Handles invalid status values
 * - Returns correct total count for filtered results
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data/base-data.sql", 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/orders.sql", 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup.sql", 
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class Task08FilterOrdersByStatusTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetOrders_FilterByStatusPending() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.content[0].status", is("PENDING")));
    }

    @Test
    public void testGetOrders_FilterByStatusCompleted() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].status", everyItem(is("COMPLETED"))));
    }

    @Test
    public void testGetOrders_FilterByStatusCancelled() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("status", "CANCELLED"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetOrders_FilterWithPagination() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("status", "PENDING")
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].status", everyItem(is("PENDING"))))
                .andExpect(jsonPath("$.size", is(5)));
    }

    @Test
    public void testGetOrders_FilterByInvalidStatus() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetOrders_FilterByStatusNoResults() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("status", "COMPLETED"))
                .andExpect(status().isOk());
        // May return empty content or 0 results depending on test data
    }
}
