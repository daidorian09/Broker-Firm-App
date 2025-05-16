package com.brokage.firm.application.dto.filter;

import java.math.BigDecimal;
import java.util.UUID;

public record AssetFilter(
        UUID customerId,
        String assetName,
        BigDecimal minUsableSize,
        BigDecimal minTotalSize
) {
}
