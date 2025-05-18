package com.brokage.firm.domain.entity;

import com.brokage.firm.domain.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;
import java.util.UUID;

@Getter
public class Customer {
    private final UUID id;
    private final String password;
    private final String email;
    private final UserRole role;

    @Builder
    public Customer(final UUID id, final String password, final String email, final UserRole role) {
        this.id = Optional.ofNullable(id).orElse(UUID.randomUUID());
        this.password = password;
        this.email = email;
        this.role = Optional.ofNullable(role).orElse(UserRole.CUSTOMER);
    }
}