package com.brokage.firm.domain.service;

import com.brokage.firm.domain.entity.Customer;

import java.util.Optional;

public interface CustomerRepository {

    Optional<Customer> findByEmail(final String email);

    void save(final Customer customer);
}