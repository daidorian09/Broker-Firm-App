package com.brokage.firm.api.controller;

import com.brokage.firm.application.dto.request.CreateCustomerRequest;
import com.brokage.firm.application.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Operation(
            summary = "Register a new customer",
            description = "Creates a new customer with CUSTOMER role. Email must be unique."
    )
    @PostMapping
    public ResponseEntity<Void> register(@RequestBody final CreateCustomerRequest request) {
        customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}