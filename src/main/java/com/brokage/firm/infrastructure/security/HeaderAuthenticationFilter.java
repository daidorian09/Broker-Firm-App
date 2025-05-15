package com.brokage.firm.infrastructure.security;

import com.brokage.firm.application.constant.SecurityConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final String DELIMITER = ":";
    private static final int AUTHENTICATION_SCHEME_LIMIT = 2;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader(SecurityConstant.AUTHORIZATION_HEADER);

        if (isValidBasicAuth(authorizationHeader)
                && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
            final Authentication auth = new UsernamePasswordAuthenticationToken(
                    UUID.randomUUID().toString(),
                    null,
                    List.of(new SimpleGrantedAuthority(SecurityConstant.USER_ROLE))
            );
            ((UsernamePasswordAuthenticationToken) auth).setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        final String path = request.getRequestURI();
        return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
    }

    private boolean isValidBasicAuth(final String authorizationHeader) {
        if (StringUtils.isBlank(authorizationHeader) || !authorizationHeader.startsWith(SecurityConstant.AUTHENTICATION_SCHEME)) {
            return false;
        }

        try {
            final String base64Credentials = authorizationHeader.substring(SecurityConstant.AUTHENTICATION_SCHEME.length());
            final byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            final String credentials = new String(decodedBytes, StandardCharsets.UTF_8);

            final String[] parts = credentials.split(DELIMITER, AUTHENTICATION_SCHEME_LIMIT);
            if (parts.length != AUTHENTICATION_SCHEME_LIMIT) return false;

            final String username = parts[0];
            final String password = parts[1];

            return StringUtils.equals(username, SecurityConstant.BASIC_AUTH_USERNAME) && StringUtils.equals(password, SecurityConstant.BASIC_AUTH_PASSWORD);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}