package com.brokage.firm.domain.exception;

public class PasswordLengthInvalidException extends BaseBrokerFirmException {
    public PasswordLengthInvalidException(final String password) {
        super("Given password length is invalid: %s".formatted(password));
    }
}
