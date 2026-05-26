package com.project.bangjjack.domain.chat.application.dto.response;

import java.time.LocalDateTime;

public record ReadReceiptBroadcast(
        WebSocketBroadcastType type,
        Long roomId,
        Long readerId,
        Long lastReadMessageId,
        LocalDateTime readAt
) {
    public static ReadReceiptBroadcast of(Long roomId, Long readerId, Long lastReadMessageId) {
        return new ReadReceiptBroadcast(WebSocketBroadcastType.READ_RECEIPT, roomId, readerId, lastReadMessageId, LocalDateTime.now());
    }
}
