package com.brokage.firm.api.controller;

import com.brokage.firm.application.configuration.SecurityConfig;
import com.brokage.firm.application.dto.request.LoginRequest;
import com.brokage.firm.application.dto.response.TokenResponse;
import com.brokage.firm.application.service.impl.AuthServiceImpl;
import com.brokage.firm.domain.exception.AuthenticationFailedException;
import com.brokage.firm.infrastructure.security.JwtAuthenticationFilter;
import com.brokage.firm.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthServiceImpl authService;

    @MockBean
    private JwtService jwtService;

    private final String loginJson = """
        {
          "email": "test@example.com",
          "password": "123456"
        }
        """;

    @Test
    void login_shouldReturnTokenResponse_whenCredentialsAreValid() throws Exception {
        final String expectedToken = "jwt.token.value";
        final TokenResponse tokenResponse = new TokenResponse(expectedToken);

        when(authService.authenticate(any(LoginRequest.class)))
                .thenReturn(tokenResponse);

        // Act + Assert
        mockMvc.perform(post("/api/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken));
    }

    @Test
    void login_shouldReturn401_whenAuthenticationFails() throws Exception {
        when(authService.authenticate(any(LoginRequest.class)))
                .thenThrow(new AuthenticationFailedException("Invalid credentials"));

        mockMvc.perform(post("/api/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("AuthenticationFailedException"))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }
}