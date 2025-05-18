package com.brokage.firm.api.controller;

import com.brokage.firm.application.constant.PaginationConstant;
import com.brokage.firm.application.dto.filter.OrderFilter;
import com.brokage.firm.application.dto.request.CreateOrderRequest;
import com.brokage.firm.application.dto.request.OrderFilterRequest;
import com.brokage.firm.application.service.OrderService;
import com.brokage.firm.domain.entity.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@SecurityRequirement(name = "basicAuth")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create a stock order",
            description = "Creates a new stock order (BUY or SELL) for a given customer and asset.",
            security = {@SecurityRequirement(name = "basicAuth"), @SecurityRequirement(name = "bearerAuth")})
    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody final CreateOrderRequest request) {
        orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "List stock orders",
            description = "Lists all stock orders for a given customer. Date range (from-to) is optional.",
            security = {@SecurityRequirement(name = "basicAuth"), @SecurityRequirement(name = "bearerAuth")})
    @GetMapping
    public ResponseEntity<Page<Order>> listOrders(@ParameterObject final OrderFilterRequest request,
                                                  @ParameterObject @PageableDefault(size = PaginationConstant.DEFAULT_PAGE_SIZE,
                                                          sort = PaginationConstant.DEFAULT_SORTING_FIELD,
                                                          direction = Sort.Direction.DESC) final Pageable pageable) {
        return ResponseEntity.ok(orderService.listOrders(new OrderFilter(request.customerId(),
                request.from(),
                request.to(),
                request.orderSide(),
                request.status(),
                request.minSize(),
                request.maxPrice()), pageable));
    }

    @Operation(summary = "Cancel an order",
            description = "Cancels a PENDING stock order by its ID. Only PENDING orders can be canceled.",
            security = {@SecurityRequirement(name = "basicAuth"), @SecurityRequirement(name = "bearerAuth")})
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable final UUID orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}