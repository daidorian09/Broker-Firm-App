package com.brokage.firm.domain.entity;

import com.brokage.firm.domain.enums.OrderSide;
import com.brokage.firm.domain.enums.OrderStatus;
import com.brokage.firm.domain.exception.OrderCannotBeCanceledException;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Getter
public class Order {

    private final UUID id;
    private final UUID customerId;
    private final String assetName;
    private final OrderSide orderSide;
    private final BigDecimal size;
    private final BigDecimal price;
    private OrderStatus status;
    private final LocalDateTime createDate;
    private LocalDateTime updateDate;

    @Builder
    public Order(UUID id, UUID customerId, String assetName, OrderSide orderSide,
                 BigDecimal size, BigDecimal price, OrderStatus status, LocalDateTime createDate) {
        this.id = Optional.ofNullable(id).orElse(UUID.randomUUID());
        this.customerId = customerId;
        this.assetName = assetName;
        this.orderSide = orderSide;
        this.size = size;
        this.price = price;
        this.status = Optional.ofNullable(status).orElse(OrderStatus.PENDING);
        this.createDate = Optional.ofNullable(createDate).orElse(LocalDateTime.now());
    }

    public static Order create(final UUID customerId,
                               final String assetName,
                               final OrderSide side,
                               final BigDecimal size,
                               final BigDecimal price) {
        return Order.builder()
                .customerId(customerId)
                .assetName(assetName)
                .orderSide(side)
                .size(size)
                .price(price)
                .build();
    }

    public void cancel() {
        if (status == OrderStatus.CANCELED) { //For idempotency
            return;
        }

        if (!Objects.equals(status, OrderStatus.PENDING)) {
            throw new OrderCannotBeCanceledException(status);
        }

        this.status = OrderStatus.CANCELED;
        this.updateDate = LocalDateTime.now();
    }
}
