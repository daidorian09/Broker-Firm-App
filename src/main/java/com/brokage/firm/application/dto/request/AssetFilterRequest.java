package com.brokage.firm.application.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import org.springdoc.core.annotations.ParameterObject;

import java.math.BigDecimal;
import java.util.UUID;

@ParameterObject
public record AssetFilterRequest(
        @Parameter(description = "Customer UUID", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID customerId,

        @Parameter(description = "Asset name (optional)", example = "TRY")
        String assetName,

        @Parameter(description = "Minimum usable size (optional)", example = "100")
        BigDecimal minUsableSize,

        @Parameter(description = "Minimum total size (optional)", example = "200")
        BigDecimal minTotalSize
) {
}