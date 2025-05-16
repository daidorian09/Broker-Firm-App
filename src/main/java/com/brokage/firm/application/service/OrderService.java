package com.brokage.firm.application.service;

import com.brokage.firm.application.dto.filter.OrderFilter;
import com.brokage.firm.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {

    void createOrder(final UUID customerId, final String assetName, final String orderSide,
                      final String size, final String price);

    Page<Order> listOrders(final OrderFilter request, final Pageable pageable);

    void cancelOrder(final UUID orderId);
}
