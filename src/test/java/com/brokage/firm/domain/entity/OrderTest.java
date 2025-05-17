package com.brokage.firm.domain.entity;

import com.brokage.firm.domain.enums.OrderSide;
import com.brokage.firm.domain.enums.OrderStatus;
import com.brokage.firm.domain.exception.OrderCannotBeCanceledException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void create_shouldInitializeWithPendingStatusAndRandomId() {
        final UUID customerId = UUID.randomUUID();
        final Order order = Order.create(customerId, "TRY", OrderSide.BUY, BigDecimal.TEN, BigDecimal.valueOf(100));

        assertThat(order.getId()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getCreateDate()).isNotNull();
        assertThat(order.getCustomerId()).isEqualTo(customerId);
        assertThat(order.getAssetName()).isEqualTo("TRY");
        assertThat(order.getSize()).isEqualByComparingTo("10");
        assertThat(order.getPrice()).isEqualByComparingTo("100");
    }

    @Test
    void cancel_shouldSetStatusToCanceledAndSetUpdateDate() {
        final Order order = Order.create(UUID.randomUUID(), "TRY", OrderSide.SELL, BigDecimal.ONE, BigDecimal.TEN);

        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(order.getUpdateDate()).isNotNull();
    }

    @Test
    void cancel_shouldBeIdempotentIfAlreadyCanceled() {
        final Order order = Order.create(UUID.randomUUID(), "TRY", OrderSide.SELL, BigDecimal.ONE, BigDecimal.TEN);

        order.cancel();
        final LocalDateTime firstUpdate = order.getUpdateDate();

        order.cancel(); // second call, should not change state

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(order.getUpdateDate()).isEqualTo(firstUpdate); // no new update
    }

    @Test
    void cancel_shouldThrowOrderCannotBeCanceledExceptionIfStatusNotPending() {
        final Order order = Order.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .assetName("TRY")
                .orderSide(OrderSide.BUY)
                .size(BigDecimal.ONE)
                .price(BigDecimal.TEN)
                .status(OrderStatus.MATCHED)
                .createDate(LocalDateTime.now())
                .build();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(OrderCannotBeCanceledException.class)
                .hasMessage("Only PENDING orders can be canceled. Current status: MATCHED");
    }

    @Test
    void builder_shouldDefaultIdStatusAndCreateDateIfNull() {
        final Order order = Order.builder()
                .customerId(UUID.randomUUID())
                .assetName("TRY")
                .orderSide(OrderSide.SELL)
                .size(BigDecimal.ONE)
                .price(BigDecimal.TEN)
                .build();

        assertThat(order.getId()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getCreateDate()).isNotNull();
    }
}