package com.brokage.firm.domain.entity;

import lombok.Builder;
import lombok.Getter;

import java.util.Optional;
import java.util.UUID;

@Getter
public class Customer {
    private final UUID id;
    private final String username;
    private final String password;

    @Builder
    public Customer(UUID id, String username, String password) {
        this.id = Optional.ofNullable(id).orElse(UUID.randomUUID());
        this.username = username;
        this.password = password;
    }

    public static Customer create(final String username, final String password) {
        return Customer.builder()
                .username(username)
                .password(password)
                .build();
    }
}
