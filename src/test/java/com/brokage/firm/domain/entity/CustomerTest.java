package com.brokage.firm.domain.entity;

import com.brokage.firm.domain.enums.UserRole;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    @Test
    void shouldBuildCustomer_withAllFields() {
        final UUID id = UUID.randomUUID();
        final String email = "test@example.com";
        final String password = "hashed-password";
        final UserRole role = UserRole.ADMIN;

        Customer customer = Customer.builder()
                .id(id)
                .email(email)
                .password(password)
                .role(role)
                .build();

        assertThat(customer.getId()).isEqualTo(id);
        assertThat(customer.getEmail()).isEqualTo(email);
        assertThat(customer.getPassword()).isEqualTo(password);
        assertThat(customer.getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void shouldGenerateId_whenIdIsNull() {
        final Customer customer = Customer.builder()
                .email("generated@example.com")
                .password("pass")
                .build();

        assertThat(customer.getId()).isNotNull();
    }

    @Test
    void shouldSetDefaultRoleToCustomer_whenRoleIsNull() {
        final Customer customer = Customer.builder()
                .email("defaultrole@example.com")
                .password("pass")
                .build();

        assertThat(customer.getRole()).isEqualTo(UserRole.CUSTOMER);
    }
}