package com.brokage.firm.domain.exception;

import com.brokage.firm.domain.enums.OrderStatus;

public class OrderCannotBeCanceledException extends BaseBrokerFirmException {
    public OrderCannotBeCanceledException(final OrderStatus status) {
        super("Only PENDING orders can be canceled. Current status: " + status);
    }
}