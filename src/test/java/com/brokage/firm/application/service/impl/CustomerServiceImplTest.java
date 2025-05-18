package com.brokage.firm.application.service.impl;

import com.brokage.firm.application.dto.request.CreateCustomerRequest;
import com.brokage.firm.domain.entity.Customer;
import com.brokage.firm.domain.exception.CustomerAlreadyExistsException;
import com.brokage.firm.domain.exception.InvalidEmailFormatException;
import com.brokage.firm.domain.exception.PasswordLengthInvalidException;
import com.brokage.firm.domain.service.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void shouldCreateCustomerSuccessfully_whenValidRequest() {
        // given
        String rawPassword = "strong123";
        String encodedPassword = "encoded";
        CreateCustomerRequest request = new CreateCustomerRequest("valid@example.com", rawPassword);

        when(customerRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // when
        customerService.createCustomer(request);

        // then
        verify(customerRepository).save(argThat(customer ->
                customer.getEmail().equals(request.email()) &&
                        customer.getPassword().equals(encodedPassword)
        ));
    }

    @Test
    void shouldThrowInvalidEmailFormatException_whenEmailIsInvalid() {
        CreateCustomerRequest request = new CreateCustomerRequest("invalid-email", "password123");

        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(InvalidEmailFormatException.class)
                .hasMessageContaining("invalid-email");
    }

    @Test
    void shouldThrowPasswordLengthInvalidException_whenPasswordTooShort() {
        CreateCustomerRequest request = new CreateCustomerRequest("test@example.com", "123");

        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(PasswordLengthInvalidException.class)
                .hasMessageContaining("123");
    }

    @Test
    void shouldThrowCustomerAlreadyExistsException_whenEmailExists() {
        CreateCustomerRequest request = new CreateCustomerRequest("exists@example.com", "password123");

        when(customerRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(Customer.builder().email(request.email()).build()));

        assertThatThrownBy(() -> customerService.createCustomer(request))
                .isInstanceOf(CustomerAlreadyExistsException.class)
                .hasMessageContaining("exists@example.com");
    }
}