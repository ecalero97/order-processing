package com.ecalero.order.actor.message;

import com.ecalero.order.domain.model.Order;

public record OrderSavedMessage(Order orderSaved, ProcessOrderMessage originalMessage) {
}
