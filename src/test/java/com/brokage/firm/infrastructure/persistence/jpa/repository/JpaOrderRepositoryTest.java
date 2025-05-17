package com.brokage.firm.infrastructure.persistence.jpa.repository;

import com.brokage.firm.domain.enums.OrderSide;
import com.brokage.firm.domain.enums.OrderStatus;
import com.brokage.firm.infrastructure.persistence.jpa.entity.OrderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JpaOrderRepositoryTest {

    @Autowired
    private JpaOrderRepository orderRepository;

    @Test
    void saveAndFindById_shouldPersistOrderEntity() {
        final UUID id = UUID.randomUUID();
        final OrderEntity entity = OrderEntity.builder()
                .id(id)
                .customerId(UUID.randomUUID())
                .assetName("TRY")
                .orderSide(OrderSide.BUY)
                .size(BigDecimal.valueOf(2))
                .price(BigDecimal.valueOf(1200))
                .status(OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();

        orderRepository.save(entity);

        final Optional<OrderEntity> result = orderRepository.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getAssetName()).isEqualTo("TRY");
        assertThat(result.get().getOrderSide()).isEqualTo(OrderSide.BUY);
    }
}
