package com.ecalero.order.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class OrderCountResponse {
    private OffsetDateTime from;
    private OffsetDateTime to;
    private long totalOrders;
}
