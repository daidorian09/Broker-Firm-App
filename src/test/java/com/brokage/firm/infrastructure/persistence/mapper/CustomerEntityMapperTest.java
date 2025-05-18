package com.brokage.firm.infrastructure.persistence.mapper;

import com.brokage.firm.domain.entity.Customer;
import com.brokage.firm.domain.enums.UserRole;
import com.brokage.firm.infrastructure.persistence.jpa.entity.CustomerEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerEntityMapperTest {

    @Test
    void shouldMapEntityToDomainCorrectly() {
        // given
        final CustomerEntity entity = CustomerEntity.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .password("encoded-pass")
                .role(UserRole.CUSTOMER)
                .build();

        // when
        final Customer domain = CustomerEntityMapper.toDomain(entity);

        // then
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getEmail()).isEqualTo(entity.getEmail());
        assertThat(domain.getPassword()).isEqualTo(entity.getPassword());
        assertThat(domain.getRole()).isEqualTo(entity.getRole());
    }

    @Test
    void shouldMapDomainToEntityCorrectly() {
        // given
        final Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .password("encoded-pass")
                .role(UserRole.ADMIN)
                .build();

        // when
        final CustomerEntity entity = CustomerEntityMapper.toEntity(customer);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(customer.getId());
        assertThat(entity.getEmail()).isEqualTo(customer.getEmail());
        assertThat(entity.getPassword()).isEqualTo(customer.getPassword());
        assertThat(entity.getRole()).isEqualTo(customer.getRole());
    }

    @Test
    void shouldReturnNull_whenEntityIsNull() {
        assertThat(CustomerEntityMapper.toDomain(null)).isNull();
    }

    @Test
    void shouldReturnNull_whenDomainIsNull() {
        assertThat(CustomerEntityMapper.toEntity(null)).isNull();
    }
}