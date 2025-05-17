package com.brokage.firm.domain.exception;

import java.math.BigDecimal;

public class InsufficientUsableAssetSizeException extends BaseBrokerFirmException {
    public InsufficientUsableAssetSizeException(final BigDecimal available, final BigDecimal requested) {
        super("Insufficient usable asset size. Available: %s, Requested: %s".formatted(available, requested));
    }
}
