package com.brokage.firm.application.dto.request;

import java.util.UUID;

public record CreateOrderRequest(
        UUID customerId,
        String assetName,
        String orderSide,
        String size,
        String price
) {
}