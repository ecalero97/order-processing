package com.ecalero.order.domain.repository;

import com.ecalero.order.domain.model.Order;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Repository
public interface OrderRepository extends ReactiveMongoRepository<Order, ObjectId> {
    Mono<Order> findByOrderId(String orderId);

    Flux<Order> findByTsBetween(OffsetDateTime from, OffsetDateTime to);
}