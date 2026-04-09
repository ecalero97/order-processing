package com.ecalero.order.domain.service;

import com.ecalero.order.api.dto.OrderCountResponse;
import com.ecalero.order.api.dto.OrderStatusResponse;
import com.ecalero.order.api.exception.InvalidDateRangeException;
import com.ecalero.order.api.exception.OrderNotFoundException;
import com.ecalero.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Mono<OrderStatusResponse> getOrderStatus(String orderId) {
        log.info("Checking status for order {}", orderId);
        return orderRepository.findByOrderId(orderId)
                .map(order -> OrderStatusResponse.builder()
                        .orderId(order.getOrderId())
                        .customerId(order.getCustomerId())
                        .customerPhoneNumber(order.getCustomerPhoneNumber())
                        .status(order.getStatus())
                        .items(order.getItems())
                        .ts(order.getTs())
                        .build())
                .switchIfEmpty(Mono.error(new OrderNotFoundException("Order not found")));
    }

    public Mono<OrderCountResponse> countOrdersByDateRange(OffsetDateTime from, OffsetDateTime to) {
        this.validate(from, to);
        log.info("Count orders between {} and {}", from, to);
        return orderRepository.findByTsBetween(from, to)
                .count()
                .map(count -> OrderCountResponse.builder()
                        .from(from)
                        .to(to)
                        .totalOrders(count)
                        .build());
    }


    private void validate(OffsetDateTime from, OffsetDateTime to) {
        if (from == null || to == null) {
            throw new InvalidDateRangeException("'from' and 'to' are required");
        }
        if (from.isAfter(to)) {
            throw new InvalidDateRangeException("'from' must be before 'to'");
        }
        if (from.isEqual(to)) {
            throw new InvalidDateRangeException("'from' and 'to' cannot be equal");
        }
    }
}
