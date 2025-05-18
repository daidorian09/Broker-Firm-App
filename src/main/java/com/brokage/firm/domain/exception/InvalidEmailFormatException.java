package com.brokage.firm.domain.exception;

public class InvalidEmailFormatException extends BaseBrokerFirmException {
    public InvalidEmailFormatException(final String emailAddress) {
        super("Invalid email format for %s given email address".formatted(emailAddress));
    }
}
