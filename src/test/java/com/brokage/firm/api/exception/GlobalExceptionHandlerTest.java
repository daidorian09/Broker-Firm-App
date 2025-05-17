package com.brokage.firm.api.exception;


import com.brokage.firm.domain.exception.BaseBrokerFirmException;
import com.brokage.firm.domain.exception.LockAcquisitionException;
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
}
