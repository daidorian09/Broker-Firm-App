package com.brokage.firm.api.controller;

import com.brokage.firm.application.configuration.SecurityConfig;
import com.brokage.firm.application.constant.SecurityConstant;
import com.brokage.firm.application.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void createOrder_shouldReturn201Created() throws Exception {
        final String requestBody = """
            {
                "customerId": "5a5da567-5315-4839-aefe-9a0b3589a354",
                "assetName": "TRY",
                "orderSide": "BUY",
                "size": 100,
                "price": 50.5
            }
        """;

        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content(requestBody)
                        .with(httpBasic(SecurityConstant.BASIC_AUTH_USERNAME, SecurityConstant.BASIC_AUTH_PASSWORD)))
                .andExpect(status().isCreated());
    }

    @Test
    void createOrder_shouldReturn403_whenAuthorizationIsMissing() throws Exception {
        final String requestBody = """
        {
            "customerId": "5a5da567-5315-4839-aefe-9a0b3589a354",
            "assetName": "TRY",
            "orderSide": "BUY",
            "size": 100,
            "price": 50.5
        }
    """;

        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }


    @Test
    void listOrders_shouldReturnPagedOrders() throws Exception {
        final UUID customerId = UUID.randomUUID();

        mockMvc.perform(get("/api/orders")
                        .param("customerId", customerId.toString())
                        .param("page", "0")
                        .param("size", "10")
                        .with(httpBasic(SecurityConstant.BASIC_AUTH_USERNAME, SecurityConstant.BASIC_AUTH_PASSWORD)))
                .andExpect(status().isOk());
    }

    @Test
    void cancelOrder_shouldReturn204NoContent() throws Exception {
        final UUID orderId = UUID.randomUUID();

        mockMvc.perform(delete("/api/orders/{orderId}", orderId)
                        .with(httpBasic(SecurityConstant.BASIC_AUTH_USERNAME, SecurityConstant.BASIC_AUTH_PASSWORD)))
                .andExpect(status().isNoContent());
    }
}
