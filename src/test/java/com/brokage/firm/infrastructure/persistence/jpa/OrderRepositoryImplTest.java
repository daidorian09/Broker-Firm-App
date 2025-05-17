package com.brokage.firm.infrastructure.persistence.jpa;

import com.brokage.firm.application.dto.filter.OrderFilter;
import com.brokage.firm.domain.entity.Order;
import com.brokage.firm.domain.enums.OrderSide;
import com.brokage.firm.domain.enums.OrderStatus;
import com.brokage.firm.infrastructure.persistence.jpa.entity.OrderEntity;
import com.brokage.firm.infrastructure.persistence.jpa.repository.JpaOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {

    @Mock
    private JpaOrderRepository jpaOrderRepository;

    private OrderRepositoryImpl orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new OrderRepositoryImpl(jpaOrderRepository);
    }

    @Test
    void save_shouldCallJpaWithMappedEntity() {
        final Order domainOrder = Order.create(
                UUID.randomUUID(),
                "TRY",
                OrderSide.BUY,
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(100)
        );

        orderRepository.save(domainOrder);

        verify(jpaOrderRepository).save(ArgumentMatchers.any(OrderEntity.class));
    }

    @Test
    void findById_shouldReturnMappedDomainOrder() {
        final UUID id = UUID.randomUUID();
        final OrderEntity entity = OrderEntity.builder()
                .id(id)
                .customerId(UUID.randomUUID())
                .assetName("BTC")
                .orderSide(OrderSide.SELL)
                .size(BigDecimal.TEN)
                .price(BigDecimal.valueOf(50))
                .status(OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();

        when(jpaOrderRepository.findById(id)).thenReturn(Optional.of(entity));

        final Optional<Order> result = orderRepository.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getAssetName()).isEqualTo("BTC");
    }

    @Test
    void findByFilters_shouldReturnMappedPage() {
        final UUID customerId = UUID.randomUUID();
        final OrderFilter filter = new OrderFilter(customerId, null, null, null, null, null, null);
        final Pageable pageable = PageRequest.of(0, 5);

        final OrderEntity entity = OrderEntity.builder()
                .id(UUID.randomUUID())
                .customerId(customerId)
                .assetName("TRY")
                .orderSide(OrderSide.BUY)
                .size(BigDecimal.ONE)
                .price(BigDecimal.TEN)
                .status(OrderStatus.PENDING)
                .createDate(LocalDateTime.now())
                .build();

        when(jpaOrderRepository.findAll(ArgumentMatchers.any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(entity)));

        final Page<Order> result = orderRepository.findByFilters(filter, pageable);

        assertThat(result.getSize()).isOne();
        assertThat(result.getContent().get(0).getAssetName()).isEqualTo("TRY");
    }
}