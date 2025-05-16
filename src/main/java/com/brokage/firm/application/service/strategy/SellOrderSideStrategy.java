package com.brokage.firm.application.service.strategy;

import com.brokage.firm.application.dto.OrderSideExecutionRequest;
import com.brokage.firm.application.service.AssetService;
import com.brokage.firm.domain.enums.OrderSide;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SellOrderSideStrategy extends AbstractOrderSideExecutionStrategy implements OrderSideExecutionStrategy {

    private final AssetService assetService;

    @Override
    public boolean isMatched(final OrderSide side) {
        return Objects.equals(side, OrderSide.SELL);
    }

    @Override
    public void execute(final OrderSideExecutionRequest request) {
        assetService.reserveAsset(
                request.customerId(),
                request.assetName(),
                super.getRequiredAmount(request.size(), request.price()));
    }
}
