package com.brokage.firm.application.dto;

import com.brokage.firm.domain.enums.UserRole;

import java.io.Serializable;
import java.util.UUID;

public record CustomUserPrincipal(UUID customerId, String email, UserRole role) implements Serializable {
}

