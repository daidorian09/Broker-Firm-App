package com.brokage.firm.infrastructure.security.impl;

import com.brokage.firm.application.configuration.BrokerApplicationConfig;
import com.brokage.firm.application.dto.CustomUserPrincipal;
import com.brokage.firm.domain.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @Mock
    private BrokerApplicationConfig brokerApplicationConfig;

    @Mock
    private BrokerApplicationConfig.SecurityConfig securityConfig;

    private JwtServiceImpl jwtService;

    private static final String SECRET_KEY = "very-secret-key-that-is-at-least-256-bits-long-123456";

    @BeforeEach
    void setUp() {
        when(brokerApplicationConfig.getSecurityConfig()).thenReturn(securityConfig);
        when(securityConfig.getJwtSecretKey()).thenReturn(SECRET_KEY);
        jwtService = new JwtServiceImpl(brokerApplicationConfig);
    }

    @Test
    void shouldGenerateAndParseTokenCorrectly() {
        final UUID userId = UUID.randomUUID();
        final String email = "test@example.com";
        final UserRole role = UserRole.CUSTOMER;

        final CustomUserPrincipal principal = new CustomUserPrincipal(userId, email, role);

        String token = jwtService.generateToken(principal);
        CustomUserPrincipal parsedPrincipal = jwtService.parseToken(token);

        assertEquals(principal.customerId(), parsedPrincipal.customerId());
        assertEquals(principal.email(), parsedPrincipal.email());
        assertEquals(principal.role(), parsedPrincipal.role());
    }

    @Test
    void shouldThrowException_whenTokenIsInvalid() {
        String invalidToken = "this.is.an.invalid.token";

        assertThrows(io.jsonwebtoken.JwtException.class, () -> jwtService.parseToken(invalidToken));
    }
}