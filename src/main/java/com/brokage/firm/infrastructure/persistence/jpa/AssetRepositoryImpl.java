package com.brokage.firm.infrastructure.persistence.jpa;

import com.brokage.firm.application.dto.filter.AssetFilter;
import com.brokage.firm.domain.entity.Asset;
import com.brokage.firm.domain.service.AssetRepository;
import com.brokage.firm.infrastructure.persistence.jpa.repository.JpaAssetRepository;
import com.brokage.firm.infrastructure.persistence.jpa.repository.JpaCustomerRepository;
import com.brokage.firm.infrastructure.persistence.jpa.spec.AssetSpecification;
import com.brokage.firm.infrastructure.persistence.mapper.AssetEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AssetRepositoryImpl implements AssetRepository {

    private final JpaAssetRepository jpaAssetRepository;

    @Override
    public Page<Asset> findByFilters(final AssetFilter filter, final Pageable pageable) {
        return jpaAssetRepository
                .findAll(AssetSpecification.byFilter(filter), pageable)
                .map(AssetEntityMapper::toDomain);
    }

    @Override
    public Optional<Asset> findByCustomerIdAndAssetName(UUID customerId, String assetName) {
        return jpaAssetRepository
                .findByCustomerIdAndAssetName(customerId, assetName)
                .map(AssetEntityMapper::toDomain);
    }

    @Override
    public void save(final Asset asset) {
        jpaAssetRepository.save(AssetEntityMapper.toEntity(asset));
    }
}