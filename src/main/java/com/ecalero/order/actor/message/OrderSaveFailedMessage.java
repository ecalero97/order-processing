package com.ecalero.order.actor.message;

public record OrderSaveFailedMessage(ProcessOrderMessage originalMessage, Throwable cause) {
}