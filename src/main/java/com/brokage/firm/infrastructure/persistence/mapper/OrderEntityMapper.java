package com.brokage.firm.infrastructure.persistence.mapper;

import com.brokage.firm.domain.entity.Order;
import com.brokage.firm.infrastructure.persistence.jpa.entity.OrderEntity;

import java.util.Objects;

public class OrderEntityMapper {

    public static Order toDomain(OrderEntity entity) {
        if (Objects.isNull(entity)) return null;

        return Order.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .assetName(entity.getAssetName())
                .orderSide(entity.getOrderSide())
                .size(entity.getSize())
                .price(entity.getPrice())
                .status(entity.getStatus())
                .createDate(entity.getCreateDate())
                .build();
    }

    public static OrderEntity toEntity(Order domain) {
        if (Objects.isNull(domain)) return null;

        return OrderEntity.builder()
                .id(domain.getId())
                .customerId(domain.getCustomerId())
                .assetName(domain.getAssetName())
                .orderSide(domain.getOrderSide())
                .size(domain.getSize())
                .price(domain.getPrice())
                .status(domain.getStatus())
                .createDate(domain.getCreateDate())
                .build();
    }
}
