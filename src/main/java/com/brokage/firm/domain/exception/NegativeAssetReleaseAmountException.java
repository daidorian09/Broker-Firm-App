package com.brokage.firm.domain.exception;

import java.math.BigDecimal;

public class NegativeAssetReleaseAmountException extends BaseBrokerFirmException {
    public NegativeAssetReleaseAmountException(final BigDecimal attempted) {
        super("Release amount must be positive. Attempted: " + attempted);
    }
}