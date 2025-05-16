package com.brokage.firm.infrastructure.persistence.jpa.repository;

import com.brokage.firm.infrastructure.persistence.jpa.entity.AssetEntity;
import com.brokage.firm.infrastructure.persistence.jpa.entity.AssetId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaAssetRepository extends JpaRepository<AssetEntity, AssetId>,
        JpaSpecificationExecutor<AssetEntity> {
    Optional<AssetEntity> findByCustomerIdAndAssetName(final UUID customerId, final String assetName);
}
