package com.project.bangjjack.domain.chat.application.dto.request;

public record WebSocketInboundMessage(
        WebSocketMessageType type,
        Long roomId,
        String content,
        String token
) {
}
