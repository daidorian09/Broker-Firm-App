package com.brokage.firm.infrastructure.persistence.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetId implements Serializable {
    private static final long serialVersionUID = 6969601858097320299L;

    private UUID customerId;
    private String assetName;
}
