package com.brokage.firm.infrastructure.persistence.mapper;

import com.brokage.firm.domain.entity.Customer;
import com.brokage.firm.infrastructure.persistence.jpa.entity.CustomerEntity;

import java.util.Objects;

public class CustomerEntityMapper {

    public static Customer toDomain(final CustomerEntity entity) {
        if (Objects.isNull(entity)) return null;

        return Customer.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .role(entity.getRole())
                .build();
    }

    public static CustomerEntity toEntity(final Customer customer) {
        if (Objects.isNull(customer)) return null;

        return CustomerEntity.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .password(customer.getPassword())
                .role(customer.getRole())
                .build();
    }
}