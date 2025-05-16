package com.brokage.firm.application.service.impl;

import com.brokage.firm.application.configuration.BrokerApplicationConfig;
import com.brokage.firm.application.dto.filter.AssetFilter;
import com.brokage.firm.application.service.AssetService;
import com.brokage.firm.application.service.LockService;
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

    private static final String ASSET_LOCK_RESERVE_KEY_FORMAT = "asset-lock::%s::%s::reserve";
    private static final String ASSET_LOCK_CREATE_KEY_FORMAT = "asset-lock::%s::%s::release";
    public static final String ASSET_LOCK_RELEASE_KEY_FORMAT = "asset-lock::%s::%s::create";

    private final AssetRepository assetRepository;
    private final LockService lockService;
    private final BrokerApplicationConfig currencyProperties;

    @Override
    @Transactional
    public Page<Asset> listAssets(final AssetFilter filter, final Pageable pageable) {
        return assetRepository.findByFilters(filter, pageable);
    }

    @Override
    public void reserveAsset(final UUID customerId, final String assetName, final BigDecimal amount) {
        final String lockKey = ASSET_LOCK_RESERVE_KEY_FORMAT.formatted(customerId, assetName);

        lockService.executeWithLock(lockKey, () -> {
            final Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                    .orElseThrow(() -> new IllegalArgumentException("Asset not found: " + assetName));

            assetRepository.save(asset.reserve(amount));
        });
    }

    @Override
    public void creditOrCreateCustomerAsset(final UUID customerId, final String assetName, final BigDecimal amount) {

        checkCurrencyAllowedForAssetCreation(assetName);

        final String lockKey = ASSET_LOCK_CREATE_KEY_FORMAT.formatted(customerId, assetName);

        lockService.executeWithLock(lockKey, () -> {

            final Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                    .orElseGet(() -> Asset.builder()
                            .customerId(customerId)
                            .assetName(assetName)
                            .totalSize(BigDecimal.ZERO)
                            .usableSize(BigDecimal.ZERO)
                            .build()
                    );

            assetRepository.save(asset.increase(amount));
        });
    }

    @Override
    @Transactional
    public void releaseReservedAsset(final UUID customerId, final String assetName, final BigDecimal amount) {
        final String lockKey = ASSET_LOCK_RELEASE_KEY_FORMAT.formatted(customerId, assetName);

        lockService.executeWithLock(lockKey, () -> {
            final Asset asset = assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                    .orElseThrow(() -> new IllegalStateException("Asset not found: " + assetName));

            assetRepository.save(asset.release(amount));
        });
    }


    private void checkCurrencyAllowedForAssetCreation(final String assetName) {
        if (currencyProperties.getCurrencies()
                .stream()
                .noneMatch(c -> StringUtils.equalsIgnoreCase(c, assetName))) {
            throw new IllegalArgumentException("Asset auto-create not allowed: " + assetName);
        }
    }
}
