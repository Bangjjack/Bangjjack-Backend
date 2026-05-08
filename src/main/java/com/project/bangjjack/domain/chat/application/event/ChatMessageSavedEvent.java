package com.project.bangjjack.domain.chat.application.event;

import java.time.LocalDateTime;

public record ChatMessageSavedEvent(
        Long messageId,
        Long roomId,
        Long senderId,
        String senderNickname,
        String senderProfileImage,
        String content,
        LocalDateTime createdAt
) {
}
