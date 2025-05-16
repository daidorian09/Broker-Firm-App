package com.brokage.firm.domain.service;

import com.brokage.firm.domain.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    void save(final Order order);

    Optional<Order> findById(final UUID id);

    List<Order> findByCustomerIdAndDateRange(final UUID customerId, final LocalDateTime from, final LocalDateTime to);
}
