package com.brokage.firm.application.service.impl;

import com.brokage.firm.application.dto.CustomUserPrincipal;
import com.brokage.firm.application.dto.request.LoginRequest;
import com.brokage.firm.application.dto.response.TokenResponse;
import com.brokage.firm.domain.entity.Customer;
import com.brokage.firm.domain.enums.UserRole;
import com.brokage.firm.domain.exception.AuthenticationFailedException;
import com.brokage.firm.domain.service.CustomerRepository;
import com.brokage.firm.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private final String email = "user@example.com";
    private final String password = "rawPassword";
    private final String encodedPassword = "encodedPassword";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnToken_whenCredentialsAreValid() {
        // Arrange
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(encodedPassword)
                .role(UserRole.CUSTOMER)
                .build();

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtService.generateToken(any(CustomUserPrincipal.class))).thenReturn("jwt-token");

        // Act
        TokenResponse response = authService.authenticate(new LoginRequest(email, password));

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("jwt-token");

        verify(customerRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(jwtService).generateToken(any(CustomUserPrincipal.class));
    }

    @Test
    void shouldThrowException_whenEmailNotFound() {
        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.authenticate(new LoginRequest(email, password)))
                .isInstanceOf(AuthenticationFailedException.class)
                .hasMessage("Email not found");

        verify(customerRepository).findByEmail(email);
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    void shouldThrowException_whenPasswordDoesNotMatch() {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .email(email)
                .password(encodedPassword)
                .role(UserRole.CUSTOMER)
                .build();

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        assertThatThrownBy(() -> authService.authenticate(new LoginRequest(email, password)))
                .isInstanceOf(AuthenticationFailedException.class)
                .hasMessage("Invalid password");

        verify(passwordEncoder).matches(password, encodedPassword);
        verify(jwtService, never()).generateToken(any());
    }
}