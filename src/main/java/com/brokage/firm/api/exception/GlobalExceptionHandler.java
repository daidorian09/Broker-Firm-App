package com.brokage.firm.api.exception;

import com.brokage.firm.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(BaseBrokerFirmException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleDomainExceptions(final BaseBrokerFirmException ex) {
        log.warn("Exception occurred: {} - {}",
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                ex);

        return ResponseEntity.badRequest().body(buildBody(
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        ));
    }

    @ExceptionHandler(LockAcquisitionException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleLockAcquisitionException(final Exception ex) {
        log.error("LockAcquisitionException occurred : ", ex);

        return ResponseEntity.internalServerError().body(buildBody(
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        ));
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleAuthenticationFailedException(final AuthenticationFailedException ex) {
        log.warn("AuthenticationFailedException occurred: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildBody(
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED
        ));
    }

    @ExceptionHandler(CustomerAlreadyExistsException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleCustomerAlreadyExistsException(final CustomerAlreadyExistsException ex) {
        log.warn("CustomerAlreadyExistsException occurred: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildBody(
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                HttpStatus.CONFLICT
        ));
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleUnauthorizedAccessException(final UnauthorizedAccessException ex) {
        log.warn("UnauthorizedAccessException occurred: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(buildBody(
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                HttpStatus.FORBIDDEN
        ));
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleGenericException(final Exception ex) {
        log.error("Unexpected exception occurred : ", ex);

        return ResponseEntity.internalServerError().body(buildBody(
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        ));
    }

    private Map<String, Object> buildBody(final String error, final String message, final HttpStatus status) {
        return Map.of(
                "timestamp", FORMATTER.format(LocalDateTime.now()),
                "error", error,
                "message", message,
                "status", status.value()
        );
    }
}