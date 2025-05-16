package com.brokage.firm.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(AssetId.class) // composite key: customerId + assetName
public class AssetEntity {

    @Id
    private UUID customerId;

    @Id
    private String assetName;

    private BigDecimal totalSize;

    private BigDecimal usableSize;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;
}
