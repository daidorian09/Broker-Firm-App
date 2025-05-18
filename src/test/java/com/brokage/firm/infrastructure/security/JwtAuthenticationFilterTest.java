package com.brokage.firm.infrastructure.security;

import com.brokage.firm.application.dto.CustomUserPrincipal;
import com.brokage.firm.domain.enums.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.UUID;

import static com.brokage.firm.application.constant.SecurityConstant.AUTHORIZATION_HEADER;
import static com.brokage.firm.application.constant.SecurityConstant.JWT_BEARER_AUTHENTICATION_SCHEME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueFilterChain_whenHeaderIsMissingOrInvalid() throws ServletException, IOException {
        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }


    @Test
    void shouldAuthenticate_whenValidJwtProvided() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String headerValue = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(headerValue);
        when(jwtService.parseToken(token)).thenReturn(new CustomUserPrincipal(UUID.randomUUID(), "test@mail.com", UserRole.CUSTOMER));

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSetUnauthorized_whenJwtParsingFails() throws ServletException, IOException {
        String token = "invalid.token";
        String headerValue = JWT_BEARER_AUTHENTICATION_SCHEME + token;

        when(request.getHeader("Authorization")).thenReturn(headerValue);
        when(jwtService.parseToken(token)).thenThrow(new RuntimeException("Invalid token"));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(any(), any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

}