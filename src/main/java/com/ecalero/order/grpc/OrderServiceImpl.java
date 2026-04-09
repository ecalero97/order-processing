package com.ecalero.order.grpc;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.ecalero.order.actor.OrderProcessorActor;
import com.ecalero.order.actor.message.ProcessOrderMessage;
import com.ecalero.order.domain.repository.OrderRepository;
import com.ecalero.order.smpp.SmppClient;
import com.ecalero.order.util.OrderMetrics;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {
    private final ActorSystem actorSystem;
    private final OrderRepository orderRepository;
    private final SmppClient smppClient;
    private final OrderMetrics orderMetrics;

    private ActorRef orderProcessorActor;

    @PostConstruct
    public void init() {
        this.orderProcessorActor = actorSystem.actorOf(
                OrderProcessorActor.props(orderRepository, smppClient, orderMetrics),
                "order-processor"
        );
        log.info("OrderProcessorActor created: {}", orderProcessorActor.path());
    }

    @Override
    public void createOrder(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        log.info("Receive Order {}", request.getOrderId());
        orderMetrics.incrementOrdersReceived();
        orderProcessorActor.tell(new ProcessOrderMessage(request, responseObserver), ActorRef.noSender());
    }
}
