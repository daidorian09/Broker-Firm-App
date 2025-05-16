package com.brokage.firm.domain.service;

import com.brokage.firm.application.dto.filter.AssetFilter;
import com.brokage.firm.domain.entity.Asset;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface AssetRepository {
    Page<Asset> findByFilters(final AssetFilter filter, final Pageable pageable);
    Optional<Asset> findByCustomerIdAndAssetName(final UUID customerId, final String assetName);
    void save(final Asset asset);
}
