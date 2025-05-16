package com.brokage.firm.application.service;

import com.brokage.firm.application.dto.filter.AssetFilter;
import com.brokage.firm.domain.entity.Asset;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.UUID;

public interface AssetService {
    Page<Asset> listAssets(final AssetFilter filter, final Pageable pageable);

    void reserveAsset(final UUID customerId, final String assetName, final BigDecimal amount);

    void creditOrCreateCustomerAsset(final UUID customerId, final String assetName, final BigDecimal amount);

    void releaseReservedAsset(final UUID customerId, final String assetName, final BigDecimal amount);
}
