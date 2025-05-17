package com.brokage.firm.domain.exception;

public class InvalidOrderSideException extends BaseBrokerFirmException {
    public InvalidOrderSideException(final String rawInput) {
        super("Invalid order side: " + rawInput);
    }
}
