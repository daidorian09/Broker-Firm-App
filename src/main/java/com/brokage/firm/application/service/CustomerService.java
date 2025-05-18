package com.brokage.firm.application.service;

import com.brokage.firm.application.dto.request.CreateCustomerRequest;

public interface CustomerService {
    void createCustomer(final CreateCustomerRequest request);
}

