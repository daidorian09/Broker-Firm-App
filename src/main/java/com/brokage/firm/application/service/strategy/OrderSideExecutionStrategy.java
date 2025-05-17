package com.brokage.firm.application.service.strategy;

import com.brokage.firm.application.dto.OrderSideExecutionRequest;
import com.brokage.firm.domain.enums.OrderSide;

public sealed interface OrderSideExecutionStrategy
        permits BuyOrderSideStrategy, SellOrderSideStrategy {
    boolean isMatched(final OrderSide side);

    void execute(final OrderSideExecutionRequest request);
}

