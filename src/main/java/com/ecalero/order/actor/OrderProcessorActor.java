package com.ecalero.order.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.ecalero.order.actor.message.ProcessOrderMessage;
import com.ecalero.order.domain.model.Order;
import com.ecalero.order.domain.repository.OrderRepository;
import com.ecalero.order.grpc.OrderRequest;
import com.ecalero.order.grpc.OrderResponse;
import com.ecalero.order.smpp.SmppClient;
import com.ecalero.order.util.OrderMetrics;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

import java.time.OffsetDateTime;

@Slf4j
@RequiredArgsConstructor
public class OrderProcessorActor extends AbstractActor {
    private final OrderRepository orderRepository;
    private final SmppClient smppClientService;
    private final OrderMetrics orderMetrics;

    public static Props props(OrderRepository orderRepository, SmppClient smppClient, OrderMetrics orderMetrics) {
        return Props.create(OrderProcessorActor.class, orderRepository, smppClient, orderMetrics);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ProcessOrderMessage.class, this::handleProcessOrder)
                .matchAny(msg -> log.warn("No Handler found for message: {}", msg))
                .build();
    }

    private void handleProcessOrder(ProcessOrderMessage message) {
        OrderRequest request  = message.request();
        var observer = message.responseObserver();
        String  orderId  = request.getOrderId();
        log.info("Processing Order {}", request.getOrderId());
        try {
            Order order = Order.builder()
                    .id(new ObjectId())
                    .orderId(orderId)
                    .customerId(request.getCustomerId())
                    .customerPhoneNumber(request.getCustomerPhoneNumber())
                    .status("PROCESSED")
                    .items(request.getItemsList())
                    .ts(OffsetDateTime.now())
                    .build();

            orderRepository.save(order).block();
            log.debug("Order {} saved successfully", orderId);
            String smsText = "Your order " + orderId + " has been processed";
            smppClientService.sendSms(request.getCustomerPhoneNumber(), smsText);
            orderMetrics.incrementOrdersProcessed();
            OrderResponse response = OrderResponse.newBuilder()
                    .setOrderId(orderId)
                    .setStatus("PROCESSED")
                    .build();

            observer.onNext(response);
            observer.onCompleted();
        } catch (Exception ex) {
            log.error("Error on process order with id {}", orderId, ex);
            orderMetrics.incrementOrdersError();
            observer.onError(Status.INTERNAL.withDescription("Error on process order").asRuntimeException());
        }
    }
}
