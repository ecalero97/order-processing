package com.ecalero.order.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
public class OrderStatusResponse {
    private String orderId;
    private String customerId;
    private String customerPhoneNumber;
    private String status;
    private List<String> items;
    private OffsetDateTime ts;
}
