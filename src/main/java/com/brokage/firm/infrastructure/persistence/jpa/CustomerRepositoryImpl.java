package com.brokage.firm.infrastructure.persistence.jpa;

import com.brokage.firm.domain.service.CustomerRepository;
import com.brokage.firm.domain.entity.Customer;
import com.brokage.firm.infrastructure.persistence.jpa.repository.JpaCustomerRepository;
import com.brokage.firm.infrastructure.persistence.mapper.CustomerEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepository {

    private final JpaCustomerRepository jpaRepository;

    @Override
    public Optional<Customer> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(CustomerEntityMapper::toDomain);
    }

    @Override
    public void save(final Customer customer) {
        jpaRepository.save(CustomerEntityMapper.toEntity(customer));
    }
}