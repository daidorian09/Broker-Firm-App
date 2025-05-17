package com.brokage.firm.infrastructure.persistence.mapper;

import com.brokage.firm.domain.entity.Order;
import com.brokage.firm.domain.enums.OrderSide;
import com.brokage.firm.domain.enums.OrderStatus;
import com.brokage.firm.infrastructure.persistence.jpa.entity.OrderEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderEntityMapperTest {

    @Test
    void toDomain_shouldMapEntityToDomain() {
        final UUID id = UUID.randomUUID();
        final UUID customerId = UUID.randomUUID();
        final LocalDateTime now = LocalDateTime.now();

        final OrderEntity entity = OrderEntity.builder()
                .id(id)
                .customerId(customerId)
                .assetName("TRY")
                .orderSide(OrderSide.SELL)
                .size(BigDecimal.valueOf(5))
                .price(BigDecimal.valueOf(200))
                .status(OrderStatus.PENDING)
                .createDate(now)
                .build();

        final Order domain = OrderEntityMapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getCustomerId()).isEqualTo(customerId);
        assertThat(domain.getAssetName()).isEqualTo("TRY");
        assertThat(domain.getOrderSide()).isEqualTo(OrderSide.SELL);
        assertThat(domain.getSize()).isEqualByComparingTo("5");
        assertThat(domain.getPrice()).isEqualByComparingTo("200");
        assertThat(domain.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(domain.getCreateDate()).isEqualTo(now);
    }

    @Test
    void toEntity_shouldMapDomainToEntity() {
        final UUID id = UUID.randomUUID();
        final UUID customerId = UUID.randomUUID();
        final LocalDateTime now = LocalDateTime.now();

        final Order domain = Order.builder()
                .id(id)
                .customerId(customerId)
                .assetName("TRY")
                .orderSide(OrderSide.BUY)
                .size(BigDecimal.valueOf(2))
                .price(BigDecimal.valueOf(1500))
                .status(OrderStatus.CANCELED)
                .createDate(now)
                .build();

        final OrderEntity entity = OrderEntityMapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getCustomerId()).isEqualTo(customerId);
        assertThat(entity.getAssetName()).isEqualTo("TRY");
        assertThat(entity.getOrderSide()).isEqualTo(OrderSide.BUY);
        assertThat(entity.getSize()).isEqualByComparingTo("2");
        assertThat(entity.getPrice()).isEqualByComparingTo("1500");
        assertThat(entity.getStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(entity.getCreateDate()).isEqualTo(now);
    }

    @Test
    void toDomain_shouldReturnNull_whenEntityIsNull() {
        assertThat(OrderEntityMapper.toDomain(null)).isNull();
    }

    @Test
    void toEntity_shouldReturnNull_whenDomainIsNull() {
        assertThat(OrderEntityMapper.toEntity(null)).isNull();
    }
}