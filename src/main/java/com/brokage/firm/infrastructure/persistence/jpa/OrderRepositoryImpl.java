package com.brokage.firm.infrastructure.persistence.jpa;

import com.brokage.firm.domain.entity.Order;
import com.brokage.firm.domain.service.OrderRepository;
import com.brokage.firm.infrastructure.persistence.jpa.repository.JpaOrderRepository;
import com.brokage.firm.infrastructure.persistence.mapper.OrderEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;

    @Override
    public void save(final Order order) {
        jpaOrderRepository.save(OrderEntityMapper.toEntity(order));
    }

    @Override
    public Optional<Order> findById(final UUID id) {
        return jpaOrderRepository.findById(id)
                .map(OrderEntityMapper::toDomain);
    }

    @Override
    public List<Order> findByCustomerIdAndDateRange(final UUID customerId, final LocalDateTime from, final LocalDateTime to) {
        return jpaOrderRepository.findByCustomerIdAndCreateDateBetween(customerId, from, to)
                .stream()
                .map(OrderEntityMapper::toDomain)
                .collect(Collectors.toList());
    }
}