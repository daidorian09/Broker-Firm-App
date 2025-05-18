package com.brokage.firm.api.controller;

import com.brokage.firm.application.configuration.SecurityConfig;
import com.brokage.firm.application.constant.SecurityConstant;
import com.brokage.firm.application.dto.CustomUserPrincipal;
import com.brokage.firm.application.service.CustomerService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private JwtService jwtService;

    private final String bearerToken = "Bearer dummy.jwt.token.customer";
    private final UUID customerId = UUID.randomUUID();

    @Test
    void createCustomer_shouldReturn201Created_whenAuthorizedAsAdmin() throws Exception {
        final String requestBody = """
            {
                "email": "test@test.com",
                "password": "123456"
            }
        """;

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(httpBasic(SecurityConstant.BASIC_AUTH_USERNAME, SecurityConstant.BASIC_AUTH_PASSWORD)))
                .andExpect(status().isCreated());
    }

    @Test
    void createCustomer_shouldReturn201Created_whenAuthorizedWithBearerToken() throws Exception {
        final String requestBody = """
            {
                "email": "test@test.com",
                "password": "123456"
            }
        """;

        CustomUserPrincipal principal = new CustomUserPrincipal(customerId, "test@test.com", UserRole.CUSTOMER);
        when(jwtService.parseToken("dummy.jwt.token.customer")).thenReturn(principal);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken))
                .andExpect(status().isCreated());
    }
}