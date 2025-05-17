package com.brokage.firm.application.service.impl;

import com.brokage.firm.application.dto.filter.OrderFilter;
import com.brokage.firm.application.dto.request.CreateOrderRequest;
import com.brokage.firm.application.service.AssetService;
import com.brokage.firm.application.service.LockService;
import com.brokage.firm.application.service.strategy.BuyOrderSideStrategy;
import com.brokage.firm.application.service.strategy.SellOrderSideStrategy;
import com.brokage.firm.domain.entity.Order;
import com.brokage.firm.domain.enums.OrderSide;
import com.brokage.firm.domain.enums.OrderStatus;
import com.brokage.firm.domain.exception.InvalidDecimalValueException;
import com.brokage.firm.domain.exception.InvalidOrderSideException;
import com.brokage.firm.domain.exception.OrderNotFoundException;
import com.brokage.firm.domain.service.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private LockService lockService;

    @Mock
    private AssetService assetService;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        final BuyOrderSideStrategy buyStrategy = new BuyOrderSideStrategy(assetService);
        final SellOrderSideStrategy sellStrategy = new SellOrderSideStrategy(assetService);

        orderService = new OrderServiceImpl(
                orderRepository,
                List.of(buyStrategy, sellStrategy),
                lockService,
                assetService
        );
    }

    @Test
    void createOrder_shouldExecuteMatchingStrategyForBuy_andSaveOrder() {
        final UUID customerId = UUID.randomUUID();
        final CreateOrderRequest request = new CreateOrderRequest(customerId, "TRY", "buy", "2", "500");

        orderService.createOrder(request);

        verify(assetService).creditOrCreateCustomerAsset(eq(customerId), eq("TRY"), eq(new BigDecimal("1000")));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_shouldExecuteMatchingStrategyForSell_andSaveOrder() {
        final UUID customerId = UUID.randomUUID();
        final CreateOrderRequest request = new CreateOrderRequest(customerId, "TRY", "sell", "3", "200");

        orderService.createOrder(request);

        verify(assetService).reserveAsset(eq(customerId), eq("TRY"), eq(new BigDecimal("600")));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_shouldThrowInvalidDecimalValueException_onInvalidInput() {
        CreateOrderRequest invalid = new CreateOrderRequest(UUID.randomUUID(), "TRY", "buy", "invalid", "price");

        assertThatThrownBy(() -> orderService.createOrder(invalid))
                .isInstanceOf(InvalidDecimalValueException.class)
                .hasMessageContaining("Invalid value for 'size': 'invalid'");
    }

    @Test
    void createOrder_shouldThrowInvalidOrderSideException_onInvalidInput() {
        CreateOrderRequest invalid = new CreateOrderRequest(UUID.randomUUID(), "TRY", "invalidOrderSide", "10", "price");

        assertThatThrownBy(() -> orderService.createOrder(invalid))
                .isInstanceOf(InvalidOrderSideException.class)
                .hasMessageContaining("Invalid order side: invalidOrderSide");
    }

    @Test
    void listOrders_shouldCallRepositoryWithFilter() {
        final OrderFilter filter = new OrderFilter(null, null, null, null, null, null, null);
        final Pageable pageable = PageRequest.of(0, 5);
        when(orderRepository.findByFilters(filter, pageable)).thenReturn(Page.empty());

        final Page<Order> result = orderService.listOrders(filter, pageable);

        assertThat(result).isEmpty();
        verify(orderRepository).findByFilters(filter, pageable);
    }

    @Test
    void cancelOrder_shouldChangeStatusAndUnlockAsset_ifSellOrder() {
        final UUID orderId = UUID.randomUUID();
        final Order order = Order.create(UUID.randomUUID(), "TRY", OrderSide.SELL, BigDecimal.ONE, BigDecimal.TEN);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        doAnswer(invocation -> {
            Runnable r = invocation.getArgument(1);
            r.run();
            return null;
        }).when(lockService).executeWithLock(anyString(), any(Runnable.class));

        orderService.cancelOrder(orderId);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        verify(orderRepository).save(order);
        verify(assetService).releaseReservedAsset(order.getCustomerId(), order.getAssetName(), order.getSize());
    }

    @Test
    void cancelOrder_shouldNotReleaseAsset_ifBuyOrder() {
        final UUID orderId = UUID.randomUUID();
        final Order order = Order.create(UUID.randomUUID(), "TRY", OrderSide.BUY, BigDecimal.ONE, BigDecimal.TEN);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        doAnswer(invocation -> {
            Runnable r = invocation.getArgument(1);
            r.run();
            return null;
        }).when(lockService).executeWithLock(anyString(), any(Runnable.class));

        orderService.cancelOrder(orderId);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
        verify(orderRepository).save(order);
        verify(assetService, never()).releaseReservedAsset(any(), any(), any());
    }

    @Test
    void cancelOrder_shouldThrowExceptionAndNotAcquireLock_whenOrderNotFound() {
        final UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining(orderId.toString());

        verify(lockService, never()).executeWithLock(anyString(), any(Runnable.class));
    }
}