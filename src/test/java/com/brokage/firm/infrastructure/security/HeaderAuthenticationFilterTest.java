package com.brokage.firm.infrastructure.security;

import com.brokage.firm.application.constant.SecurityConstant;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class HeaderAuthenticationFilterTest {

    private HeaderAuthenticationFilter filter;
    private FilterChain mockFilterChain;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        filter = new HeaderAuthenticationFilter();
        mockFilterChain = mock(FilterChain.class);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSetAuthenticationWhenValidBasicAuthAndValidCustomerId() throws Exception {
        final String credentials = "%s:%s".formatted(SecurityConstant.BASIC_AUTH_USERNAME, SecurityConstant.BASIC_AUTH_PASSWORD);
        final String base64Creds = Base64.getEncoder().encodeToString(credentials.getBytes());

        request.addHeader(SecurityConstant.AUTHORIZATION_HEADER, SecurityConstant.AUTHENTICATION_SCHEME + base64Creds);

        filter.doFilterInternal(request, response, mockFilterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(UUID.fromString(authentication.getPrincipal().toString())).isNotNull();

        verify(mockFilterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenMissingAuthorizationHeader() throws Exception {
        filter.doFilterInternal(request, response, mockFilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockFilterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenInvalidBase64() throws Exception {
        request.addHeader(SecurityConstant.AUTHORIZATION_HEADER, SecurityConstant.AUTHENTICATION_SCHEME + "invalid-base64");

        filter.doFilterInternal(request, response, mockFilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockFilterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenBasic64PartMissing() throws Exception {
        request.addHeader(SecurityConstant.AUTHORIZATION_HEADER, SecurityConstant.AUTHENTICATION_SCHEME);

        filter.doFilterInternal(request, response, mockFilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockFilterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenUsernameInvalid() throws Exception {
        final String invalidUsername = "invalid-username";
        final String password = SecurityConstant.BASIC_AUTH_PASSWORD;
        final String credentials = "%s:%s".formatted(invalidUsername, password);

        final String base64Creds = Base64.getEncoder().encodeToString(credentials.getBytes());

        request.addHeader(SecurityConstant.AUTHORIZATION_HEADER, SecurityConstant.AUTHENTICATION_SCHEME + base64Creds);
        filter.doFilterInternal(request, response, mockFilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockFilterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenPasswordInvalid() throws Exception {
        final String username = SecurityConstant.BASIC_AUTH_USERNAME;
        final String InvalidPassword = "invalid-password";
        final String credentials = "%s:%s".formatted(username, InvalidPassword);

        final String base64Creds = Base64.getEncoder().encodeToString(credentials.getBytes());

        request.addHeader(SecurityConstant.AUTHORIZATION_HEADER, SecurityConstant.AUTHENTICATION_SCHEME + base64Creds);

        filter.doFilterInternal(request, response, mockFilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockFilterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenAuthenticationSchemeDifferent() throws Exception {
        final String InvalidAuthenticationScheme = "Bearer token";
        request.addHeader(SecurityConstant.AUTHORIZATION_HEADER, InvalidAuthenticationScheme);

        filter.doFilterInternal(request, response, mockFilterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(mockFilterChain).doFilter(request, response);
    }

    @Test
    void shouldNotFilterSwaggerUI() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/swagger-ui/index.html");

        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void shouldNotFilterApiDocs() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/v3/api-docs");

        assertThat(filter.shouldNotFilter(request)).isTrue();
    }
}