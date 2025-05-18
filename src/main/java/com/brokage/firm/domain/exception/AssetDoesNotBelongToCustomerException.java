package com.brokage.firm.domain.exception;

import java.util.UUID;

public class AssetDoesNotBelongToCustomerException extends BaseBrokerFirmException {
    public AssetDoesNotBelongToCustomerException(final UUID customerId, final String assetName) {
        super("Given asset does not belong to customer, customerId : %s, assetName : %s".formatted(customerId, assetName));
    }
}
