package com.brokage.firm.application.dto.request;

import com.brokage.firm.domain.enums.OrderSide;
import com.brokage.firm.domain.enums.OrderStatus;
import io.swagger.v3.oas.annotations.Parameter;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@ParameterObject
public record OrderFilterRequest(

        @Parameter(description = "Customer UUID", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID customerId,

        @Parameter(description = "Start date (optional). Format: dd-MM-yyyy", example = "05-01-2025")
        @DateTimeFormat(pattern = "dd-MM-yyyy")
        LocalDate from,

        @Parameter(description = "End date (optional). Format: dd-MM-yyyy", example = "15-05-2025")
        @DateTimeFormat(pattern = "dd-MM-yyyy")
        LocalDate to,

        @Parameter(description = "Order side (BUY or SELL)", example = "BUY")
        OrderSide orderSide,

        @Parameter(description = "Order status (PENDING, MATCHED, etc.)", example = "PENDING")
        OrderStatus status,

        @Parameter(description = "Minimum size", example = "100")
        BigDecimal minSize,

        @Parameter(description = "Maximum price", example = "2000")
        BigDecimal maxPrice
) {
}

