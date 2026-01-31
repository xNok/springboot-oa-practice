package com.example.oa.controller;

import com.example.oa.dto.UpdateOrderStatusRequest;
import com.example.oa.entity.OrderStatus;
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
 * Test class for Task 10: Update Order Status
 * 
 * This test validates that the PATCH /api/orders/{id}/status endpoint:
 * - Updates order status successfully for valid transitions
 * - Returns 422 for invalid state transitions
 * - Enforces all state transition rules
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/test-data/base-data.sql", "/test-data/orders.sql"}, 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup.sql", 
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class Task10UpdateOrderStatusTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testUpdateStatus_ValidTransition_CreatedToConfirmed() throws Exception {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(OrderStatus.CONFIRMED);

        mockMvc.perform(patch("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("CONFIRMED")));
    }

    @Test
    public void testUpdateStatus_ValidTransition_ConfirmedToShipped() throws Exception {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(OrderStatus.SHIPPED);

        mockMvc.perform(patch("/api/orders/2/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SHIPPED")));
    }

    @Test
    public void testUpdateStatus_ValidTransition_ShippedToDelivered() throws Exception {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(OrderStatus.DELIVERED);

        mockMvc.perform(patch("/api/orders/3/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("DELIVERED")));
    }

    @Test
    public void testUpdateStatus_ValidTransition_CreatedToCancelled() throws Exception {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(OrderStatus.CANCELLED);

        mockMvc.perform(patch("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    public void testUpdateStatus_InvalidTransition_DeliveredToCancelled() throws Exception {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(OrderStatus.CANCELLED);

        mockMvc.perform(patch("/api/orders/4/status") // Order 4 is DELIVERED
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status", is(422)))
                .andExpect(jsonPath("$.message", containsString("transition")));
    }

    @Test
    public void testUpdateStatus_InvalidTransition_BackwardTransition() throws Exception {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(OrderStatus.CONFIRMED);

        mockMvc.perform(patch("/api/orders/3/status") // Order 3 is SHIPPED
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status", is(422)));
    }

    @Test
    public void testUpdateStatus_OrderNotFound() throws Exception {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setStatus(OrderStatus.CONFIRMED);

        mockMvc.perform(patch("/api/orders/9999/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }
}
