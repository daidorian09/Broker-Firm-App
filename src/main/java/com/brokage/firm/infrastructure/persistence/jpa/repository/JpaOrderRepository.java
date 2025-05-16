package com.brokage.firm.infrastructure.persistence.jpa.repository;

import com.brokage.firm.infrastructure.persistence.jpa.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findByCustomerIdAndCreateDateBetween(final UUID customerId, final LocalDateTime from, final LocalDateTime to);
}