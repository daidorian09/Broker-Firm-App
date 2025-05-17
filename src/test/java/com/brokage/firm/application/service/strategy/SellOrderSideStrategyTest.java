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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SellOrderSideStrategyTest {

    @Mock
    private AssetService assetService;

    @InjectMocks
    private SellOrderSideStrategy strategy;

    @Test
    void isMatched_shouldReturnTrue_whenSideIsSell() {
        assertThat(strategy.isMatched(OrderSide.SELL)).isTrue();
    }

    @Test
    void isMatched_shouldReturnFalse_whenSideIsNotSell() {
        assertThat(strategy.isMatched(OrderSide.BUY)).isFalse();
    }

    @Test
    void execute_shouldCallReserveAsset_withCorrectAmount() {
        final UUID customerId = UUID.randomUUID();
        final String assetName = "TRY";
        BigDecimal size = new BigDecimal("5");
        BigDecimal price = new BigDecimal("1000");
        BigDecimal expectedAmount = size.multiply(price); // 5000

        OrderSideExecutionRequest request = new OrderSideExecutionRequest(
                customerId, assetName, size, price, OrderSide.SELL
        );

        strategy.execute(request);

        verify(assetService).reserveAsset(customerId, assetName, expectedAmount);
    }
}
