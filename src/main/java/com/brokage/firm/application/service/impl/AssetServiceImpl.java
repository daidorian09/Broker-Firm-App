package com.brokage.firm.application.service.impl;

import com.brokage.firm.application.configuration.BrokerCurrencyProperties;
import com.brokage.firm.application.dto.filter.AssetFilter;
import com.brokage.firm.application.service.AssetService;
import com.brokage.firm.domain.entity.Asset;
import com.brokage.firm.domain.service.AssetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final BrokerCurrencyProperties currencyProperties;

    @Override
    @Transactional
    public Page<Asset> listAssets(final AssetFilter filter, final Pageable pageable) {
        return assetRepository.findByFilters(filter, pageable);
    }

    @Override
    public void reserveAsset(final UUID customerId, final String assetName, final BigDecimal amount) {
        final Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + assetName));

        assetRepository.save(asset.reserve(amount));
    }

    @Override
    public void creditOrCreateCustomerAsset(final UUID customerId, final String assetName, final BigDecimal amount) {

        checkCurrencyAllowedForAssetCreation(assetName);

        final Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .orElseGet(() -> Asset.builder()
                        .customerId(customerId)
                        .assetName(assetName)
                        .totalSize(BigDecimal.ZERO)
                        .usableSize(BigDecimal.ZERO)
                        .build()
                );
        assetRepository.save(asset.increase(amount));
    }

    private void checkCurrencyAllowedForAssetCreation(final String assetName) {
        if (currencyProperties.getCurrencies()
                .stream()
                .noneMatch(c -> StringUtils.equalsIgnoreCase(c, assetName))) {
            throw new IllegalArgumentException("Asset auto-create not allowed: " + assetName);
        }
    }

}
