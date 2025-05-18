package com.brokage.firm.application.service.impl;

import com.brokage.firm.application.dto.OrderSideExecutionRequest;
import com.brokage.firm.application.dto.filter.OrderFilter;
import com.brokage.firm.application.dto.request.CreateOrderRequest;
import com.brokage.firm.application.service.AssetService;
import com.brokage.firm.application.service.LockService;
import com.brokage.firm.application.service.OrderService;
import com.brokage.firm.application.service.strategy.OrderSideExecutionStrategy;
import com.brokage.firm.domain.entity.Order;
import com.brokage.firm.domain.enums.OrderSide;
import com.brokage.firm.domain.exception.OrderNotFoundException;
import com.brokage.firm.domain.service.OrderRepository;
import com.brokage.firm.infrastructure.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final String ORDER_CANCEL_LOCK_KEY_FORMAT = "order-lock::%s::cancel";

    private final OrderRepository orderRepository;
    private final List<OrderSideExecutionStrategy> orderSideExecutionStrategies;
    private final LockService lockService;
    private final AssetService assetService;

    @Override
    @Transactional
    public void createOrder(final CreateOrderRequest request) {

        final OrderSide side = request.toOrderSide();
        final BigDecimal size = request.toSize();
        final BigDecimal price = request.toPrice();

        orderSideExecutionStrategies.stream()
                .filter(s -> s.isMatched(side))
                .findFirst()
                .orElseThrow()
                .execute(new OrderSideExecutionRequest(request.customerId(), request.assetName(), size, price, side));

        final Order order = Order.create(request.customerId(), request.assetName(), side, size, price);

        orderRepository.save(order);
    }

    @Override
    public Page<Order> listOrders(final OrderFilter request, Pageable pageable) {
        Page<Order> orders = orderRepository.findByFilters(request, pageable);

        orders.stream()
                .findFirst()
                .ifPresent(order -> SecurityUtil.assertOwnershipOrAdmin(order.getCustomerId()));

        return orders;
    }

    @Override
    @Transactional
    public void cancelOrder(final UUID orderId) {
        final Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        SecurityUtil.assertOwnershipOrAdmin(order.getCustomerId());

        final String lockKey = ORDER_CANCEL_LOCK_KEY_FORMAT.formatted(orderId);
        lockService.executeWithLock(lockKey, () -> {
            order.cancel();
            orderRepository.save(order);
            releaseReservedAssetIfSellOrder(order);
        });
    }

    private void releaseReservedAssetIfSellOrder(final Order order) {
        Optional.of(order)
                .filter(o -> Objects.equals(o.getOrderSide(), OrderSide.SELL))
                .ifPresent(o -> assetService.releaseReservedAsset(o.getCustomerId(),
                        o.getAssetName(),
                        o.getSize()));
    }
}