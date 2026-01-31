package com.example.oa.controller;

import com.example.oa.dto.OrderRequest;
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
 * Test class for Task 5: Create Order
 * 
 * This test validates that the POST /api/orders endpoint:
 * - Creates a new order successfully
 * - Returns 201 Created status
 * - Sets orderDate to current timestamp
 * - Sets initial status to CREATED
 * - Validates required fields
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data/base-data.sql", 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup.sql", 
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class Task05CreateOrderTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateOrder_Success() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);
        request.setCustomerName("John Doe");
        request.setTotalAmount(2029.97);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.customerId", is(1)))
                .andExpect(jsonPath("$.customerName", is("John Doe")))
                .andExpect(jsonPath("$.totalAmount", is(2029.97)))
                .andExpect(jsonPath("$.status", is("CREATED")))
                .andExpect(jsonPath("$.orderDate", notNullValue()));
    }

    @Test
    public void testCreateOrder_ValidationError_MissingCustomerId() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setCustomerName("John Doe");
        request.setTotalAmount(2029.97);
        // Missing customerId

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    public void testCreateOrder_ValidationError_InvalidTotalAmount() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);
        request.setCustomerName("John Doe");
        request.setTotalAmount(-100.0); // Invalid: negative amount

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    public void testCreateOrder_InitialStatusIsCreated() throws Exception {
        OrderRequest request = new OrderRequest();
        request.setCustomerId(2L);
        request.setCustomerName("Jane Smith");
        request.setTotalAmount(599.99);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("CREATED")));
    }
}
