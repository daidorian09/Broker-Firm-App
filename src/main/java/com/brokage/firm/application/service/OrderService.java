package com.brokage.firm.application.service;

import com.brokage.firm.application.dto.filter.OrderFilter;
import com.brokage.firm.application.dto.request.CreateOrderRequest;
import com.brokage.firm.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {

    void createOrder(final CreateOrderRequest request);

    Page<Order> listOrders(final OrderFilter request, final Pageable pageable);

    void cancelOrder(final UUID orderId);
}