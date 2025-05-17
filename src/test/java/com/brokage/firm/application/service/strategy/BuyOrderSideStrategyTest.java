package com.brokage.firm.application.service.strategy;

import com.brokage.firm.application.dto.OrderSideExecutionRequest;
import com.brokage.firm.application.service.AssetService;
import com.brokage.firm.domain.enums.OrderSide;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BuyOrderSideStrategyTest {

    @Mock
    private AssetService assetService;

    @InjectMocks
    private BuyOrderSideStrategy strategy;

    @Test
    void isMatched_shouldReturnTrue_whenSideIsBuy() {
        assertThat(strategy.isMatched(OrderSide.BUY)).isTrue();
    }

    @Test
    void isMatched_shouldReturnFalse_whenSideIsNotBuy() {
        assertThat(strategy.isMatched(OrderSide.SELL)).isFalse();
    }

    @Test
    void execute_shouldCallCreditOrCreateCustomerAsset_withCorrectAmount() {
        final UUID customerId = UUID.randomUUID();
        final BigDecimal size = new BigDecimal("2");
        final BigDecimal price = new BigDecimal("100");
        final String assetName = "TRY";

        OrderSideExecutionRequest request = new OrderSideExecutionRequest(
                customerId, assetName, size, price, OrderSide.BUY
        );

        // Act
        strategy.execute(request);

        // Assert
        verify(assetService).creditOrCreateCustomerAsset(
                eq(customerId),
                eq(assetName),
                eq(new BigDecimal(200))
        );
    }
}
