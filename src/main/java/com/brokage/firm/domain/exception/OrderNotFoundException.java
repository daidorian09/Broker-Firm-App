package com.brokage.firm.domain.exception;

import java.util.UUID;

public class OrderNotFoundException extends BaseBrokerFirmException {
    public OrderNotFoundException(final UUID orderId) {
        super("Given order is not found: " + orderId);
    }
}
