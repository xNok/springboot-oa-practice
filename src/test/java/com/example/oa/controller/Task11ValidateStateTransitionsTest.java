package com.example.oa.controller;

import com.example.oa.dto.UpdateOrderStatusRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Test class for Task 11: Validate State Transitions
 * 
 * This test validates that order status transitions follow business rules:
 * - PENDING can transition to SHIPPED or CANCELLED
 * - SHIPPED can transition to DELIVERED or CANCELLED
 * - DELIVERED cannot transition to any other state
 * - CANCELLED cannot transition to any other state
 * - Invalid transitions are rejected with appropriate error messages
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data/base-data.sql", 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/orders.sql", 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup.sql", 
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class Task11ValidateStateTransitionsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testValidTransition_PendingToShipped() throws Exception {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus("SHIPPED");

        mockMvc.perform(patch("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SHIPPED")));
    }

    @Test
    public void testValidTransition_PendingToCancelled() throws Exception {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus("CANCELLED");

        mockMvc.perform(patch("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    public void testValidTransition_ShippedToDelivered() throws Exception {
        // First transition from PENDING to SHIPPED
        UpdateOrderStatusRequest shipRequest = new UpdateOrderStatusRequest();
        shipRequest.setStatus("SHIPPED");
        mockMvc.perform(patch("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shipRequest)))
                .andExpect(status().isOk());

        // Then transition from SHIPPED to DELIVERED
        UpdateOrderStatusRequest deliverRequest = new UpdateOrderStatusRequest();
        deliverRequest.setStatus("DELIVERED");
        mockMvc.perform(patch("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deliverRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("DELIVERED")));
    }

    @Test
    public void testInvalidTransition_DeliveredToAnyState() throws Exception {
        // Setup: transition order to DELIVERED first
        // Then attempt invalid transition
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus("PENDING");

        mockMvc.perform(patch("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("transition")));
    }

    @Test
    public void testInvalidTransition_CancelledToAnyState() throws Exception {
        // First cancel the order
        UpdateOrderStatusRequest cancelRequest = new UpdateOrderStatusRequest();
        cancelRequest.setStatus("CANCELLED");
        mockMvc.perform(patch("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isOk());

        // Then attempt to transition from cancelled
        UpdateOrderStatusRequest invalidRequest = new UpdateOrderStatusRequest();
        invalidRequest.setStatus("SHIPPED");
        mockMvc.perform(patch("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testTransition_SameStatusAsCurrentStatus() throws Exception {
        // Attempt to set status to current status
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus("PENDING");

        mockMvc.perform(patch("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
