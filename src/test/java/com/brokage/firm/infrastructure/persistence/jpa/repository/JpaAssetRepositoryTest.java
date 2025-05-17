package com.brokage.firm.infrastructure.persistence.jpa.repository;

import com.brokage.firm.infrastructure.persistence.jpa.entity.AssetEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JpaAssetRepositoryTest {

    @Autowired
    private JpaAssetRepository assetRepository;

    @Test
    void findByCustomerIdAndAssetName_shouldReturnAssetEntity() {
        UUID customerId = UUID.randomUUID();
        String assetName = "TRY";

        final AssetEntity entity = AssetEntity.builder()
                .customerId(customerId)
                .assetName(assetName)
                .totalSize(BigDecimal.valueOf(100))
                .usableSize(BigDecimal.valueOf(80))
                .build();

        assetRepository.save(entity);

        final Optional<AssetEntity> result = assetRepository.findByCustomerIdAndAssetName(customerId, assetName);

        assertThat(result).isPresent();
        assertThat(result.get().getAssetName()).isEqualTo("TRY");
        assertThat(result.get().getUsableSize()).isEqualByComparingTo("80");
    }
}