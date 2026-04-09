package com.ecalero.order.domain.model;

import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
@Document(collection = "orders")
public class Order {
    @Id
    private ObjectId id;
    private String orderId;
    private String customerId;
    private String customerPhoneNumber;
    private String status;
    private List<String> items;
    private OffsetDateTime ts;
}
