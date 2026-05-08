package com.project.bangjjack.domain.chat.application.dto.response;

import java.time.LocalDateTime;

public record ChatMessageAckResponse(
        Long messageId,
        String clientTempId,
        LocalDateTime createdAt
) {
}
