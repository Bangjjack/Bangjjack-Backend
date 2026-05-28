package com.project.bangjjack.domain.chat.application.event;

public record ReadReceiptEvent(Long roomId, Long readerId, Long lastReadMessageId) {
}
