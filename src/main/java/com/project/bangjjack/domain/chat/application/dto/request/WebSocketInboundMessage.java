package com.project.bangjjack.domain.chat.application.dto.request;

import com.project.bangjjack.domain.chat.application.exception.InvalidWsMessageFormatException;

public record WebSocketInboundMessage(
        WebSocketMessageType type,
        Long roomId,
        String content,
        Long messageId
) {
    public void validate() {
        if (roomId == null) {
            throw new InvalidWsMessageFormatException();
        }
        if (type == WebSocketMessageType.READ && messageId == null) {
            throw new InvalidWsMessageFormatException();
        }
    }
}
