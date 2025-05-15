package com.brokage.firm.application.service.impl;

import com.brokage.firm.application.service.OrderService;
import com.brokage.firm.domain.entity.Order;
import com.brokage.firm.domain.enums.OrderSide;
import com.brokage.firm.domain.service.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public Order createOrder(final UUID customerId, final String assetName, final String orderSideStr,
                             final String sizeStr, final String priceStr) {

        OrderSide side;
        BigDecimal size;
        BigDecimal price;

        try {
            side = OrderSide.valueOf(orderSideStr.toUpperCase());
            size = new BigDecimal(sizeStr);
            price = new BigDecimal(priceStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid input data: " + e.getMessage());
        }

        final Order order = Order.create(customerId, assetName, side, size, price);

        orderRepository.save(order);
        return order;
    }

    @Override
    public List<Order> listOrders(final UUID customerId, final LocalDate from, LocalDate to) {
        final LocalDateTime fromDateTime = Optional.
                ofNullable(from)
                .map(LocalDate::atStartOfDay)
                .orElse(LocalDateTime.MIN);

       final LocalDateTime toDateTime = Optional.
                ofNullable(to)
                .map(t -> t.plusDays(1).atStartOfDay().minusNanos(1))
                .orElse(LocalDateTime.now());

        return orderRepository.findByCustomerIdAndDateRange(customerId, fromDateTime, toDateTime);
    }

    @Override
    @Transactional
    public void cancelOrder(final UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.cancel();
        orderRepository.save(order);
    }
}
