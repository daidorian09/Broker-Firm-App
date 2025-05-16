package com.brokage.firm.application.service.strategy;

import java.math.BigDecimal;

public abstract class AbstractOrderSideExecutionStrategy {
    protected BigDecimal getRequiredAmount(final BigDecimal size, final BigDecimal price) {
        return size.multiply(price);
    }
}
