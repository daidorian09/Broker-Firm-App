package com.brokage.firm.domain.service;

import com.brokage.firm.application.dto.filter.OrderFilter;
import com.brokage.firm.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    void save(final Order order);

    Optional<Order> findById(final UUID id);

    Page<Order> findByFilters(final OrderFilter filter, final Pageable pageable);
}