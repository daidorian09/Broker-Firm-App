package com.brokage.firm.application.service;

import com.brokage.firm.domain.entity.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    Order createOrder(final UUID customerId, final String assetName, final String orderSide,
                      final String size, final String price);

    List<Order> listOrders(final UUID customerId, final LocalDate from, final LocalDate to);

    void cancelOrder(final UUID orderId);
}
