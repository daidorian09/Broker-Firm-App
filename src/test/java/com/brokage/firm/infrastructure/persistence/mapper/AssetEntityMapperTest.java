package com.brokage.firm.infrastructure.persistence.mapper;

import com.brokage.firm.domain.entity.Asset;
import com.brokage.firm.infrastructure.persistence.jpa.entity.AssetEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AssetEntityMapperTest {

    @Test
    void toDomain_shouldMapEntityToDomain() {
        final UUID customerId = UUID.randomUUID();

        final AssetEntity entity = AssetEntity.builder()
                .customerId(customerId)
                .assetName("TRY")
                .totalSize(BigDecimal.valueOf(100))
                .usableSize(BigDecimal.valueOf(80))
                .build();

        Asset asset = AssetEntityMapper.toDomain(entity);

        assertThat(asset).isNotNull();
        assertThat(asset.getCustomerId()).isEqualTo(customerId);
        assertThat(asset.getAssetName()).isEqualTo("TRY");
        assertThat(asset.getTotalSize()).isEqualByComparingTo("100");
        assertThat(asset.getUsableSize()).isEqualByComparingTo("80");
    }

    @Test
    void toEntity_shouldMapDomainToEntity() {
       final UUID customerId = UUID.randomUUID();

        final Asset asset = Asset.builder()
                .customerId(customerId)
                .assetName("TRY")
                .totalSize(BigDecimal.valueOf(150))
                .usableSize(BigDecimal.valueOf(120))
                .build();

        AssetEntity entity = AssetEntityMapper.toEntity(asset);

        assertThat(entity).isNotNull();
        assertThat(entity.getCustomerId()).isEqualTo(customerId);
        assertThat(entity.getAssetName()).isEqualTo("TRY");
        assertThat(entity.getTotalSize()).isEqualByComparingTo("150");
        assertThat(entity.getUsableSize()).isEqualByComparingTo("120");
    }

    @Test
    void toDomain_shouldReturnNull_whenEntityIsNull() {
        assertThat(AssetEntityMapper.toDomain(null)).isNull();
    }

    @Test
    void toEntity_shouldReturnNull_whenDomainIsNull() {
        assertThat(AssetEntityMapper.toEntity(null)).isNull();
    }
}