package com.brokage.firm.domain.exception;

public class InvalidDecimalValueException extends BaseBrokerFirmException {
    public InvalidDecimalValueException(final String field, final String rawInput) {
        super("Invalid value for '%s': '%s'".formatted(field, rawInput));
    }
}
