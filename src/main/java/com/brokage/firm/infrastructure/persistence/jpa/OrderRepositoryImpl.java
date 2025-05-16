package com.brokage.firm.infrastructure.persistence.jpa;

import com.brokage.firm.application.dto.filter.OrderFilter;
import com.brokage.firm.domain.entity.Order;
import com.brokage.firm.domain.service.OrderRepository;
import com.brokage.firm.infrastructure.persistence.jpa.repository.JpaOrderRepository;
import com.brokage.firm.infrastructure.persistence.jpa.spec.OrderSpecification;
import com.brokage.firm.infrastructure.persistence.mapper.OrderEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

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
    public Page<Order> findByFilters(final OrderFilter filter, final Pageable pageable) {
        return jpaOrderRepository
                .findAll(OrderSpecification.byFilter(filter), pageable)
                .map(OrderEntityMapper::toDomain);
    }
}