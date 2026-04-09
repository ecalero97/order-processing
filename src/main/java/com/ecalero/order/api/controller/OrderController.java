package com.ecalero.order.api.controller;

import com.ecalero.order.api.dto.OrderCountResponse;
import com.ecalero.order.api.dto.OrderStatusResponse;
import com.ecalero.order.domain.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{orderId}/status")
    public Mono<OrderStatusResponse> getOrderStatus(@PathVariable String orderId) {
        return orderService.getOrderStatus(orderId);
    }

    @GetMapping("/count")
    public Mono<OrderCountResponse> countOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {
        return orderService.countOrdersByDateRange(from, to);
    }
}
