package com.ecalero.order.actor.message;

import com.ecalero.order.grpc.OrderRequest;
import com.ecalero.order.grpc.OrderResponse;
import io.grpc.stub.StreamObserver;

public record ProcessOrderMessage(
        OrderRequest request,
        StreamObserver<OrderResponse> responseObserver
) {

}