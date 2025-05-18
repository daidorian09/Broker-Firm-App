package com.brokage.firm.infrastructure.persistence.jpa;


import com.brokage.firm.domain.entity.Customer;
import com.brokage.firm.domain.enums.UserRole;
import com.brokage.firm.infrastructure.persistence.jpa.entity.CustomerEntity;
import com.brokage.firm.infrastructure.persistence.jpa.repository.JpaCustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CustomerRepositoryImplTest {

    private JpaCustomerRepository jpaRepository;
    private CustomerRepositoryImpl customerRepository;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(JpaCustomerRepository.class);
        customerRepository = new CustomerRepositoryImpl(jpaRepository);
    }

    @Test
    void shouldReturnCustomer_whenEmailExists() {
        final UUID id = UUID.randomUUID();
        final String email = "test@example.com";
        final String password = "secret";
        final UserRole role = UserRole.CUSTOMER;

        final CustomerEntity entity = CustomerEntity.builder()
                .id(id)
                .email(email)
                .password(password)
                .role(role)
                .build();

        when(jpaRepository.findByEmail(email)).thenReturn(Optional.of(entity));

        final Optional<Customer> result = customerRepository.findByEmail(email);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
        assertThat(result.get().getPassword()).isEqualTo(password);
        assertThat(result.get().getRole()).isEqualTo(role);
    }

    @Test
    void shouldReturnEmpty_whenEmailNotFound() {
        final String email = "notfound@example.com";
        when(jpaRepository.findByEmail(email)).thenReturn(Optional.empty());

        final Optional<Customer> result = customerRepository.findByEmail(email);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldSaveCustomerEntity() {
        final Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .email("save@example.com")
                .password("encoded-pass")
                .role(UserRole.CUSTOMER)
                .build();

        customerRepository.save(customer);

        final ArgumentCaptor<CustomerEntity> captor = ArgumentCaptor.forClass(CustomerEntity.class);
        verify(jpaRepository).save(captor.capture());

        final CustomerEntity savedEntity = captor.getValue();
        assertThat(savedEntity.getEmail()).isEqualTo(customer.getEmail());
        assertThat(savedEntity.getPassword()).isEqualTo(customer.getPassword());
        assertThat(savedEntity.getRole()).isEqualTo(customer.getRole());
    }
}
