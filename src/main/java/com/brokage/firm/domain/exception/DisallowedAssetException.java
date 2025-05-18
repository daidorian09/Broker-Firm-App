package com.brokage.firm.domain.exception;

public class DisallowedAssetException extends BaseBrokerFirmException {
    public DisallowedAssetException(final String assetName) {
        super("Asset creation is not allowed for: " + assetName);
    }
}
