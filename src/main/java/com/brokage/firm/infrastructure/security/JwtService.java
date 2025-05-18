package com.brokage.firm.infrastructure.security;

import com.brokage.firm.application.dto.CustomUserPrincipal;

public interface JwtService {

    String generateToken(final CustomUserPrincipal principal);

    CustomUserPrincipal parseToken(final String token);
}