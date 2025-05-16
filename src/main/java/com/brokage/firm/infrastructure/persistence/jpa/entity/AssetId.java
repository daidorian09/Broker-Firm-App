package com.brokage.firm.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetId implements Serializable {
    private static final long serialVersionUID = 6969601858097320299L;

    private UUID customerId;
    private String assetName;
}
