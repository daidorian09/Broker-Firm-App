package com.brokage.firm.application.dto.filter;

import com.brokage.firm.domain.enums.OrderSide;
import com.brokage.firm.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record OrderFilter(
        UUID customerId,
        LocalDate from,
        LocalDate to,
        OrderSide orderSide,
        OrderStatus status,
        BigDecimal minSize,
        BigDecimal maxPrice
) {
}