package com.brokage.firm.application.service.impl;

import com.brokage.firm.application.dto.OrderSideExecutionRequest;
import com.brokage.firm.application.dto.filter.OrderFilter;
import com.brokage.firm.application.service.LockService;
import com.brokage.firm.application.service.OrderService;
import com.brokage.firm.application.service.strategy.OrderSideExecutionStrategy;
import com.brokage.firm.domain.entity.Order;
import com.brokage.firm.domain.enums.OrderSide;
import com.brokage.firm.domain.service.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final String ORDER_CANCEL_LOCK_KEY_FORMAT = "order-cancel-lock::%s";

    private final OrderRepository orderRepository;
    private final List<OrderSideExecutionStrategy> orderSideExecutionStrategies;
    private final LockService lockService;

    @Override
    @Transactional
    public void createOrder(final UUID customerId, final String assetName, final String orderSideStr,
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

        orderSideExecutionStrategies.stream()
                .filter(s -> s.isMatched(side))
                .findFirst()
                .orElseThrow()
                .execute(new OrderSideExecutionRequest(customerId, assetName, size, price, side));

        final Order order = Order.create(customerId, assetName, side, size, price);

        orderRepository.save(order);
    }

    @Override
    public Page<Order> listOrders(final OrderFilter request, Pageable pageable) {
        return orderRepository.findByFilters(request, pageable);

    }

    @Override
    @Transactional
    public void cancelOrder(final UUID orderId) {
        final String lockKey = ORDER_CANCEL_LOCK_KEY_FORMAT.formatted(orderId);

        lockService.executeWithLock(lockKey, () -> {
            final Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            order.cancel();
            orderRepository.save(order);
        });
    }
}