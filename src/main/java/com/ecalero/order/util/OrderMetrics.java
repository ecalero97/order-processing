package com.ecalero.order.util;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMetrics {

    private final MeterRegistry meterRegistry;
    private Counter ordersReceivedCounter;
    private Counter ordersProcessedCounter;
    private Counter ordersErrorCounter;

    @PostConstruct
    public void init() {
        this.ordersReceivedCounter = Counter.builder("orders_received")
                .description("Total orders received via gRPC")
                .tag(Constants.TAG_SERVICE_KEY, Constants.TAG_SERVICE_VALUE)
                .register(meterRegistry);

        this.ordersProcessedCounter = Counter.builder("orders_processed")
                .description("Total orders processed")
                .tag(Constants.TAG_SERVICE_KEY, Constants.TAG_SERVICE_VALUE)
                .register(meterRegistry);

        this.ordersErrorCounter = Counter.builder("orders_error")
                .description("Total orders with error")
                .tag(Constants.TAG_SERVICE_KEY, Constants.TAG_SERVICE_VALUE)
                .register(meterRegistry);
    }

    public void incrementOrdersReceived() {
        ordersReceivedCounter.increment();
    }

    public void incrementOrdersProcessed() {
        ordersProcessedCounter.increment();
    }

    public void incrementOrdersError() {
        ordersErrorCounter.increment();
    }
}
