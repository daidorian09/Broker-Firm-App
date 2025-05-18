package com.brokage.firm.api.controller;

import com.brokage.firm.application.dto.request.LoginRequest;
import com.brokage.firm.application.dto.response.TokenResponse;
import com.brokage.firm.application.service.impl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/authentication")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @Operation(
            summary = "Authenticate customer and generate JWT",
            description = """
                    Authenticates a customer using email and password credentials.
                    If authentication is successful, returns a JWT access token that must be included 
                    in the Authorization header for all subsequent requests (as a Bearer token).
                    
                    Example usage:
                    Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR...
                    """,
            tags = {"Authentication"}
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody final LoginRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}

