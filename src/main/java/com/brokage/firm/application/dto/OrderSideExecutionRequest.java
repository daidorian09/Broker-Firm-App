package com.brokage.firm.application.dto;

import com.brokage.firm.domain.enums.OrderSide;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderSideExecutionRequest(
        UUID customerId,
        String assetName,
        BigDecimal size,
        BigDecimal price,
        OrderSide side
) {}
