package com.brokage.firm.infrastructure.persistence.jpa.repository;

import com.brokage.firm.domain.enums.UserRole;
import com.brokage.firm.infrastructure.persistence.jpa.entity.CustomerEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JpaCustomerRepositoryTest {

    @Autowired
    private JpaCustomerRepository repository;

    @Test
    void shouldSaveAndFindCustomerByEmail() {

        final UUID id = UUID.randomUUID();
        final CustomerEntity customer = CustomerEntity.builder()
                .id(id)
                .email("test@example.com")
                .password("secure-password")
                .role(UserRole.CUSTOMER)
                .build();

        repository.save(customer);

        final Optional<CustomerEntity> result = repository.findByEmail("test@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldReturnEmpty_whenEmailNotExists() {
        final Optional<CustomerEntity> result = repository.findByEmail("notfound@example.com");

        assertThat(result).isEmpty();
    }
}