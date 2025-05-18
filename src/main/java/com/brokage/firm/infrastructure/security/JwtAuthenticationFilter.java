package com.brokage.firm.infrastructure.security;

import com.brokage.firm.application.dto.CustomUserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

import static com.brokage.firm.application.constant.SecurityConstant.AUTHORIZATION_ROLE;
import static com.brokage.firm.application.constant.SecurityConstant.JWT_BEARER_AUTHENTICATION_SCHEME;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (isValidAuthHeader(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7); //Subtrack bearer from header

        try {
            CustomUserPrincipal principal = jwtService.parseToken(token);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal, null, Collections.singletonList((new SimpleGrantedAuthority(AUTHORIZATION_ROLE + principal.role().name())))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ex) {
            log.warn("JWT parsing failed: {}", ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static boolean isValidAuthHeader(final String authHeader) {
        return StringUtils.isBlank(authHeader) || !authHeader.startsWith(JWT_BEARER_AUTHENTICATION_SCHEME);
    }
}