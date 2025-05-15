package com.brokage.firm.domain.entity;

import com.brokage.firm.domain.enums.OrderSide;
import com.brokage.firm.domain.enums.OrderStatus;
import jakarta.persistence.PrePersist;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Builder
    public Order(UUID id, UUID customerId, String assetName, OrderSide orderSide,
                 BigDecimal size, BigDecimal price, OrderStatus status, LocalDateTime createDate) {
        this.id = id != null ? id : UUID.randomUUID();
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
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be canceled.");
        }
        this.status = OrderStatus.CANCELED;
    }

    public void match() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be matched.");
        }
        this.status = OrderStatus.MATCHED;
    }
}
