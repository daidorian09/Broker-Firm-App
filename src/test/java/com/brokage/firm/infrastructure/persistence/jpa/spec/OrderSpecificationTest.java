package com.brokage.firm.infrastructure.persistence.jpa.spec;

import com.brokage.firm.application.dto.filter.OrderFilter;
import com.brokage.firm.domain.enums.OrderSide;
import com.brokage.firm.domain.enums.OrderStatus;
import com.brokage.firm.infrastructure.persistence.jpa.entity.OrderEntity;
import com.brokage.firm.infrastructure.persistence.jpa.repository.JpaOrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderSpecificationTest {

    @Autowired
    private JpaOrderRepository orderRepository;

    @Test
    void shouldFilterByCustomerIdOrderSideAndStatus() {
        final UUID customerId = UUID.randomUUID();

        orderRepository.save(OrderEntity.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .assetName("TRY")
                .orderSide(OrderSide.BUY)
                .status(OrderStatus.PENDING)
                .size(BigDecimal.TEN)
                .price(BigDecimal.valueOf(100))
                .createDate(LocalDateTime.of(2025, 5, 1, 10, 0))
                .build());

        orderRepository.save(OrderEntity.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .assetName("TRY")
                .orderSide(OrderSide.SELL)
                .status(OrderStatus.CANCELED)
                .size(BigDecimal.TEN)
                .price(BigDecimal.valueOf(200))
                .createDate(LocalDateTime.of(2025, 5, 1, 10, 0))
                .build());

        OrderFilter filter = new OrderFilter(
                customerId,
                null,
                null,
                OrderSide.BUY,
                OrderStatus.PENDING,
                null,
                null
        );

        Specification<OrderEntity> spec = OrderSpecification.byFilter(filter);
        Page<OrderEntity> result = orderRepository.findAll(spec, Pageable.ofSize(10));

        assertThat(result.getContent().size()).isOne();
        assertThat(result.getContent().get(0).getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void shouldFilterByDateRangeAndMinSizeAndMaxPrice() {
        final UUID customerId = UUID.randomUUID();

        orderRepository.save(OrderEntity.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .assetName("TRY")
                .orderSide(OrderSide.BUY)
                .status(OrderStatus.PENDING)
                .size(BigDecimal.valueOf(5))
                .price(BigDecimal.valueOf(150))
                .createDate(LocalDateTime.of(2025, 5, 5, 12, 0))
                .build());

        orderRepository.save(OrderEntity.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .assetName("TRY")
                .orderSide(OrderSide.BUY)
                .status(OrderStatus.PENDING)
                .size(BigDecimal.valueOf(1))
                .price(BigDecimal.valueOf(300))
                .createDate(LocalDateTime.of(2025, 5, 7, 12, 0))
                .build());

        final OrderFilter filter = new OrderFilter(
                null,
                LocalDate.of(2025, 5, 4),
                LocalDate.of(2025, 5, 6),
                null,
                null,
                BigDecimal.valueOf(2),
                BigDecimal.valueOf(200)
        );

        final Specification<OrderEntity> spec = OrderSpecification.byFilter(filter);
        final Page<OrderEntity> result = orderRepository.findAll(spec, Pageable.ofSize(10));

        assertThat(result.getContent().size()).isOne();
        assertThat(result.getContent().get(0).getPrice()).isLessThanOrEqualTo(BigDecimal.valueOf(200));
        assertThat(result.getContent().get(0).getSize()).isGreaterThanOrEqualTo(BigDecimal.valueOf(2));
    }
}