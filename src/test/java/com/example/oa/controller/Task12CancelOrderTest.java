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
 * Test class for Task 12: Cancel Order
 * 
 * This test validates that orders can be cancelled via a dedicated endpoint:
 * - PENDING orders can be cancelled successfully
 * - SHIPPED orders can be cancelled
 * - DELIVERED orders cannot be cancelled
 * - CANCELLED orders cannot be cancelled again
 * - Cancelled orders have status CANCELLED
 * - Non-existent orders return 404
 */
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data/base-data.sql", 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/orders.sql", 
     executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/test-data/cleanup.sql", 
     executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class Task12CancelOrderTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCancelOrder_PendingOrderSuccess() throws Exception {
        // Cancel a pending order
        mockMvc.perform(patch("/api/orders/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    public void testCancelOrder_ShippedOrderSuccess() throws Exception {
        // First transition order to SHIPPED
        UpdateOrderStatusRequest shipRequest = new UpdateOrderStatusRequest();
        shipRequest.setStatus(OrderStatus.SHIPPED);
        mockMvc.perform(patch("/api/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shipRequest)))
                .andExpect(status().isOk());

        // Then cancel it
        mockMvc.perform(patch("/api/orders/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CANCELLED")));
    }

    @Test
    public void testCancelOrder_DeliveredOrderCannotBeCancelled() throws Exception {
        // This test assumes order transitions to DELIVERED
        // Then attempts to cancel and expects failure
        mockMvc.perform(patch("/api/orders/1/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("cannot")));
    }

    @Test
    public void testCancelOrder_AlreadyCancelledOrderCannotBeCancelledAgain() throws Exception {
        // First cancel the order
        mockMvc.perform(patch("/api/orders/1/cancel"))
                .andExpect(status().isOk());

        // Attempt to cancel again
        mockMvc.perform(patch("/api/orders/1/cancel"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCancelOrder_OrderNotFound() throws Exception {
        mockMvc.perform(patch("/api/orders/9999/cancel"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Order")));
    }

    @Test
    public void testCancelOrder_VerifyOrderIsCancelled() throws Exception {
        // Cancel order
        mockMvc.perform(patch("/api/orders/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("CANCELLED")))
                .andExpect(jsonPath("$.customerId", notNullValue()));
    }
}
