package com.brokage.firm.application.service.impl;

import com.brokage.firm.application.dto.request.CreateCustomerRequest;
import com.brokage.firm.application.service.CustomerService;
import com.brokage.firm.domain.entity.Customer;
import com.brokage.firm.domain.exception.CustomerAlreadyExistsException;
import com.brokage.firm.domain.exception.InvalidEmailFormatException;
import com.brokage.firm.domain.exception.PasswordLengthInvalidException;
import com.brokage.firm.domain.service.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private static final int MINIMUM_PASSWORD_LENGTH = 6;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public void createCustomer(final CreateCustomerRequest request) {

        validateCreateCustomerRequest(request);

        final Customer customer = Customer.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        customerRepository.save(customer);
    }

    private void validateCreateCustomerRequest(final CreateCustomerRequest request) {
        if (StringUtils.isBlank(request.email()) || !EMAIL_PATTERN.matcher(request.email()).matches())
            throw new InvalidEmailFormatException(request.email());

        if (StringUtils.isBlank(request.password()) || request.password().length() < MINIMUM_PASSWORD_LENGTH)
            throw new PasswordLengthInvalidException(request.password());

        if (customerRepository.findByEmail(request.email()).isPresent())
            throw new CustomerAlreadyExistsException(request.email());
    }
}