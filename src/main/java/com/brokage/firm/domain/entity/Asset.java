package com.brokage.firm.domain.entity;

import com.brokage.firm.domain.exception.InsufficientUsableAssetSizeException;
import com.brokage.firm.domain.exception.NegativeAssetReleaseAmountException;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Getter
public class Asset {

    public static final int MINIMUM_AMOUNT = 0;
    private final UUID customerId;
    private final String assetName;
    private final BigDecimal totalSize;
    private final BigDecimal usableSize;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;

    @Builder
    public Asset(UUID customerId, String assetName, BigDecimal totalSize, BigDecimal usableSize, LocalDateTime createDate) {
        this.customerId = customerId;
        this.assetName = assetName;
        this.totalSize = totalSize;
        this.usableSize = usableSize;
        this.createDate = Optional.ofNullable(createDate).orElse(LocalDateTime.now());
        this.updateDate = null;
    }

    public Asset reserve(final BigDecimal amount) {
        if (usableSize.compareTo(amount) < MINIMUM_AMOUNT) {
            throw new InsufficientUsableAssetSizeException(usableSize, amount);
        }

        return Asset.builder()
                .customerId(customerId)
                .assetName(assetName)
                .totalSize(totalSize)
                .usableSize(usableSize.subtract(amount))
                .build();
    }

    public Asset release(final BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < MINIMUM_AMOUNT) {
            throw new NegativeAssetReleaseAmountException(amount);
        }

        return Asset.builder()
                .customerId(customerId)
                .assetName(assetName)
                .totalSize(totalSize)
                .usableSize(usableSize.add(amount))
                .build();
    }

    public Asset increase(final BigDecimal amount) {
        return Asset.builder()
                .customerId(customerId)
                .assetName(assetName)
                .totalSize(totalSize.add(amount))
                .usableSize(usableSize.add(amount))
                .build();
    }
}