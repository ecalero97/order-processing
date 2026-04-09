package com.ecalero.order.config;

import com.ecalero.order.grpc.OrderServiceImpl;
import com.ecalero.order.util.AppProperties;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.io.IOException;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class GrpcConfig {

    private final OrderServiceImpl orderGrpcService;
    private final AppProperties appProperties;
    private Server grpcServer;


    @EventListener(ContextRefreshedEvent.class)
    public void startGrpcServer() throws IOException {
        int grpcPort = appProperties.getGrpcPort();
        grpcServer = ServerBuilder
                .forPort(grpcPort)
                .addService(orderGrpcService)
                .build()
                .start();

        log.info("Starting gRPC server on port {}", grpcPort);
    }

    @EventListener(ContextClosedEvent.class)
    public void stopGrpcServer() {
        if (grpcServer != null && !grpcServer.isShutdown()) {
            log.info("Stopping gRPC server");
            grpcServer.shutdown();
        }
    }
}

