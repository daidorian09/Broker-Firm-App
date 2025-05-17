package com.brokage.firm.application.dto.request;

import com.brokage.firm.domain.enums.OrderSide;
import com.brokage.firm.domain.exception.InvalidDecimalValueException;
import com.brokage.firm.domain.exception.InvalidOrderSideException;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderRequest(
        UUID customerId,
        String assetName,
        String orderSide,
        String size,
        String price
) {

    private static final String SIZE = "size";
    private static final String PRICE = "price";

    public OrderSide toOrderSide() {
        try {
            return OrderSide.valueOf(orderSide.toUpperCase());
        } catch (Exception e) {
            throw new InvalidOrderSideException(orderSide);
        }
    }

    public BigDecimal toSize() {
        return parseDecimal(size, SIZE);
    }

    public BigDecimal toPrice() {
        return parseDecimal(price, PRICE);
    }

    private BigDecimal parseDecimal(final String value, final String fieldName) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw new InvalidDecimalValueException(fieldName, value);
        }
    }
}