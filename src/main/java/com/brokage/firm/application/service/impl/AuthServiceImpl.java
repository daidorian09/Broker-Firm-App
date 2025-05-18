package com.brokage.firm.application.service.impl;

import com.brokage.firm.application.dto.CustomUserPrincipal;
import com.brokage.firm.application.dto.request.LoginRequest;
import com.brokage.firm.application.dto.response.TokenResponse;
import com.brokage.firm.application.service.AuthService;
import com.brokage.firm.domain.entity.Customer;
import com.brokage.firm.domain.exception.AuthenticationFailedException;
import com.brokage.firm.domain.service.CustomerRepository;
import com.brokage.firm.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public TokenResponse authenticate(final LoginRequest request) {
        final Customer customer = customerRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthenticationFailedException("Email not found"));

        if (!passwordEncoder.matches(request.password(), customer.getPassword())) {
            throw new AuthenticationFailedException("Invalid password");
        }

        final CustomUserPrincipal principal = new CustomUserPrincipal(
                customer.getId(),
                customer.getEmail(),
                customer.getRole()
        );

        final String token = jwtService.generateToken(principal);
        return new TokenResponse(token);
    }
}