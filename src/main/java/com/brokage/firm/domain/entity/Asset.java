package com.brokage.firm.domain.entity;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Getter
public class Asset {

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
        if (usableSize.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient usable size.");
        }
        return Asset.builder()
                .customerId(customerId)
                .assetName(assetName)
                .totalSize(totalSize)
                .usableSize(usableSize.subtract(amount))
                .build();
    }

    public Asset release(final BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Release amount must be positive");
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
