package com.brokage.firm.api.controller;

import com.brokage.firm.application.configuration.SecurityConfig;
import com.brokage.firm.application.constant.SecurityConstant;
import com.brokage.firm.application.dto.CustomUserPrincipal;
import com.brokage.firm.application.service.OrderService;
import com.brokage.firm.domain.enums.UserRole;
import com.brokage.firm.infrastructure.security.JwtAuthenticationFilter;
import com.brokage.firm.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtService jwtService;

    private final UUID customerId = UUID.randomUUID();
    private final String bearerToken = "Bearer valid.jwt.token.order";

    private final String requestBody = """
        {
            "customerId": "%s",
            "assetName": "TRY",
            "orderSide": "BUY",
            "size": 100,
            "price": 50.5
        }
        """.formatted(customerId);

    @Test
    void createOrder_shouldReturn201_whenAuthorizedAsAdmin() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(httpBasic(SecurityConstant.BASIC_AUTH_USERNAME, SecurityConstant.BASIC_AUTH_PASSWORD)))
                .andExpect(status().isCreated());
    }

    @Test
    void createOrder_shouldReturn201_whenAuthorizedWithBearerToken() throws Exception {
        when(jwtService.parseToken("valid.jwt.token.order"))
                .thenReturn(new CustomUserPrincipal(customerId, "user@example.com", UserRole.CUSTOMER));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isCreated());
    }

    @Test
    void createOrder_shouldReturn403_whenAuthorizationIsMissing() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void listOrders_shouldReturn200_whenAuthorizedAsAdmin() throws Exception {
        mockMvc.perform(get("/api/orders")
                        .param("customerId", customerId.toString())
                        .param("page", "0")
                        .param("size", "10")
                        .with(httpBasic(SecurityConstant.BASIC_AUTH_USERNAME, SecurityConstant.BASIC_AUTH_PASSWORD)))
                .andExpect(status().isOk());
    }

    @Test
    void listOrders_shouldReturn200_whenAuthorizedWithBearerToken() throws Exception {
        when(jwtService.parseToken("valid.jwt.token.order"))
                .thenReturn(new CustomUserPrincipal(customerId, "user@example.com", UserRole.CUSTOMER));

        mockMvc.perform(get("/api/orders")
                        .param("customerId", customerId.toString())
                        .param("page", "0")
                        .param("size", "10")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isOk());
    }

    @Test
    void cancelOrder_shouldReturn204_whenAuthorizedAsAdmin() throws Exception {
        final UUID orderId = UUID.randomUUID();

        mockMvc.perform(delete("/api/orders/{orderId}", orderId)
                        .with(httpBasic(SecurityConstant.BASIC_AUTH_USERNAME, SecurityConstant.BASIC_AUTH_PASSWORD)))
                .andExpect(status().isNoContent());
    }

    @Test
    void cancelOrder_shouldReturn204_whenAuthorizedWithBearerToken() throws Exception {
        final UUID orderId = UUID.randomUUID();

        when(jwtService.parseToken("valid.jwt.token.order"))
                .thenReturn(new CustomUserPrincipal(customerId, "user@example.com", UserRole.CUSTOMER));

        mockMvc.perform(delete("/api/orders/{orderId}", orderId)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void cancelOrder_shouldReturn403_whenAuthorizationIsMissing() throws Exception {
        final UUID orderId = UUID.randomUUID();

        mockMvc.perform(delete("/api/orders/{orderId}", orderId))
                .andExpect(status().isForbidden());
    }
}
