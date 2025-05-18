package com.brokage.firm.application.dto.request;

public record CreateCustomerRequest(
        String email,
        String password
) {
}