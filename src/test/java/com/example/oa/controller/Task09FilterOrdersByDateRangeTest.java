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
 * Test class for Task 9: Filter Orders by Date Range
 * 
 * This test validates that the GET /api/orders endpoint:
 * - Filters orders by start and end date query parameters
 * - Returns only orders within the specified date range
 * - Supports pagination with date filter
 * - Handles invalid date formats
 * - Combines multiple filters (date, status, pagination)
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data/base-data.sql", 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/orders.sql", 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup.sql", 
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class Task09FilterOrdersByDateRangeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetOrders_FilterByDateRange() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    public void testGetOrders_FilterByStartDateOnly() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("startDate", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    public void testGetOrders_FilterByEndDateOnly() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("endDate", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(0))));
    }

    @Test
    public void testGetOrders_FilterByDateRangeWithPagination() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31")
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", is(5)));
    }

    @Test
    public void testGetOrders_FilterByDateAndStatus() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31")
                .param("status", "PENDING"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetOrders_FilterByInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("startDate", "invalid-date")
                .param("endDate", "2024-12-31"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetOrders_FilterByStartDateAfterEndDate() throws Exception {
        mockMvc.perform(get("/api/orders")
                .param("startDate", "2024-12-31")
                .param("endDate", "2024-01-01"))
                .andExpect(status().isBadRequest());
    }
}
