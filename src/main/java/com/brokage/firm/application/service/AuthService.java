package com.brokage.firm.application.service;

import com.brokage.firm.application.dto.request.LoginRequest;
import com.brokage.firm.application.dto.response.TokenResponse;

public interface AuthService {
    TokenResponse authenticate(final LoginRequest request);
}