package com.brokage.firm.infrastructure.persistence.mapper;

import com.brokage.firm.domain.entity.Asset;
import com.brokage.firm.infrastructure.persistence.jpa.entity.AssetEntity;

public class AssetEntityMapper {

    public static Asset toDomain(final AssetEntity entity) {
        if (entity == null) return null;

        return Asset.builder()
                .customerId(entity.getCustomerId())
                .assetName(entity.getAssetName())
                .totalSize(entity.getTotalSize())
                .usableSize(entity.getUsableSize())
                .build();
    }

    public static AssetEntity toEntity(final Asset asset) {
        if (asset == null) return null;

        return AssetEntity.builder()
                .customerId(asset.getCustomerId())
                .assetName(asset.getAssetName())
                .totalSize(asset.getTotalSize())
                .usableSize(asset.getUsableSize())
                .build();
    }
}
