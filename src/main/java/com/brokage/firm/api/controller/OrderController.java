package com.brokage.firm.api.controller;

import com.brokage.firm.application.dto.request.CreateOrderRequest;
import com.brokage.firm.application.service.OrderService;
import com.brokage.firm.domain.entity.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@SecurityRequirement(name = "basicAuth")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Create a stock order",
            description = "Creates a new stock order (BUY or SELL) for a given customer and asset.",
            security = {@SecurityRequirement(name = "basicAuth")}
    )
    @PostMapping
    public ResponseEntity<Void> createOrder(@RequestBody final CreateOrderRequest request) {
        orderService.createOrder(
                request.customerId(),
                request.assetName(),
                request.orderSide(),
                request.size(),
                request.price()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "List stock orders",
            description = "Lists all stock orders for a given customer. Date range (from-to) is optional.",
            security = {@SecurityRequirement(name = "basicAuth")}
    )
    @GetMapping
    public ResponseEntity<List<Order>> listOrders(
            @Parameter(description = "Customer UUID", required = true, example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @RequestParam final UUID customerId,

            @Parameter(description = "Start date (optional). Format: dd-MM-yyyy", example = "05-01-2025")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "MM-dd-yyyy") final LocalDate from,

            @Parameter(description = "End date (optional). Format: dd-MM-yyyy", example = "05-15-2025")
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "MM-dd-yyyy") final LocalDate to
    ) {
        return ResponseEntity.ok(orderService.listOrders(customerId, from, to));
    }

    @Operation(
            summary = "Cancel an order",
            description = "Cancels a PENDING stock order by its ID. Only PENDING orders can be canceled.",
            security = {@SecurityRequirement(name = "basicAuth")}
    )
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable final UUID orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}