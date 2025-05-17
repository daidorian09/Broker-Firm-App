package com.brokage.firm.domain.entity;

import com.brokage.firm.domain.exception.InsufficientUsableAssetSizeException;
import com.brokage.firm.domain.exception.NegativeAssetReleaseAmountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssetTest {

    private UUID customerId;
    private String assetName;
    private Asset asset;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        assetName = "TRY";
        asset = Asset.builder()
                .customerId(customerId)
                .assetName(assetName)
                .totalSize(BigDecimal.valueOf(100))
                .usableSize(BigDecimal.valueOf(80))
                .createDate(LocalDateTime.now())
                .build();
    }

    @Test
    void reserve_shouldDecreaseUsableSize() {
        final Asset updated = asset.reserve(BigDecimal.valueOf(30));

        assertThat(updated.getUsableSize()).isEqualByComparingTo("50");
        assertThat(updated.getTotalSize()).isEqualByComparingTo("100");
    }

    @Test
    void reserve_shouldThrowInsufficientUsableAssetSizeException_whenAmountExceedsUsableSize() {
        assertThatThrownBy(() -> asset.reserve(BigDecimal.valueOf(100)))
                .isInstanceOf(InsufficientUsableAssetSizeException.class)
                .hasMessage("Insufficient usable asset size. Available: 80, Requested: 100");
    }

    @Test
    void release_shouldIncreaseUsableSize() {
        final Asset updated = asset.release(BigDecimal.valueOf(20));

        assertThat(updated.getUsableSize()).isEqualByComparingTo("100");
        assertThat(updated.getTotalSize()).isEqualByComparingTo("100");
    }

    @Test
    void release_shouldThrowNegativeAssetReleaseAmountException_whenAmountIsNegative() {
        assertThatThrownBy(() -> asset.release(BigDecimal.valueOf(-10)))
                .isInstanceOf(NegativeAssetReleaseAmountException.class)
                .hasMessage("Release amount must be positive. Attempted: -10");
    }

    @Test
    void increase_shouldIncreaseBothTotalAndUsableSize() {
        final Asset updated = asset.increase(BigDecimal.valueOf(50));

        assertThat(updated.getTotalSize()).isEqualByComparingTo("150");
        assertThat(updated.getUsableSize()).isEqualByComparingTo("130");
    }

    @Test
    void builder_shouldDefaultCreateDateIfNull() {
        final Asset newAsset = Asset.builder()
                .customerId(customerId)
                .assetName(assetName)
                .totalSize(BigDecimal.ZERO)
                .usableSize(BigDecimal.ZERO)
                .createDate(null)
                .build();

        assertThat(newAsset.getCreateDate()).isNotNull();
    }
}