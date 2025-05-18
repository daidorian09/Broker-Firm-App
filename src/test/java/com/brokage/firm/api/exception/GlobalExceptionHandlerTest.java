package com.brokage.firm.api.exception;


import com.brokage.firm.domain.exception.AuthenticationFailedException;
import com.brokage.firm.domain.exception.BaseBrokerFirmException;
import com.brokage.firm.domain.exception.CustomerAlreadyExistsException;
import com.brokage.firm.domain.exception.LockAcquisitionException;
import com.brokage.firm.domain.exception.UnauthorizedAccessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleBaseBrokerFirmException() {
        final BaseBrokerFirmException ex = new BaseBrokerFirmException("Domain error occurred") {
        };
        final ResponseEntity<Map<String, Object>> response = handler.handleDomainExceptions(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", ex.getClass().getSimpleName());
        assertThat(response.getBody()).containsEntry("message", ex.getMessage());
        assertThat(response.getBody()).containsEntry("status", HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldHandleLockAcquisitionException() {
        final LockAcquisitionException ex = new LockAcquisitionException("Could not acquire lock");
        final ResponseEntity<Map<String, Object>> response = handler.handleLockAcquisitionException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("error", ex.getClass().getSimpleName());
        assertThat(response.getBody()).containsEntry("message", ex.getMessage());
        assertThat(response.getBody()).containsEntry("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void shouldHandleGenericException() {
        final Exception ex = new RuntimeException("Something went wrong");
        final ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("error", ex.getClass().getSimpleName());
        assertThat(response.getBody()).containsEntry("message", ex.getMessage());
        assertThat(response.getBody()).containsEntry("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void shouldHandleAuthenticationFailedException() {
        final AuthenticationFailedException exception = new AuthenticationFailedException("Invalid credentials");

        final ResponseEntity<Map<String, Object>> response = handler.handleAuthenticationFailedException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).containsEntry("error", "AuthenticationFailedException");
        assertThat(response.getBody()).containsEntry("message", "Invalid credentials");
        assertThat(response.getBody()).containsEntry("status", HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldHandleCustomerAlreadyExistsException() {
        final CustomerAlreadyExistsException exception = new CustomerAlreadyExistsException("Email already used");

        ResponseEntity<Map<String, Object>> response = handler.handleCustomerAlreadyExistsException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("error", "CustomerAlreadyExistsException");
        assertThat(response.getBody()).containsEntry("message", "Email already used");
        assertThat(response.getBody()).containsEntry("status", HttpStatus.CONFLICT.value());
    }

    @Test
    void shouldHandleUnauthorizedAccessException() {
        final UnauthorizedAccessException exception = new UnauthorizedAccessException("Forbidden action");

        ResponseEntity<Map<String, Object>> response = handler.handleUnauthorizedAccessException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).containsEntry("error", "UnauthorizedAccessException");
        assertThat(response.getBody()).containsEntry("message", "Forbidden action");
        assertThat(response.getBody()).containsEntry("status", HttpStatus.FORBIDDEN.value());
    }
}