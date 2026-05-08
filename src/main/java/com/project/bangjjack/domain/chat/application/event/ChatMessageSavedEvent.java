package com.project.bangjjack.domain.chat.application.event;

import com.project.bangjjack.domain.chat.domain.entity.MessageType;

import java.time.LocalDateTime;

public record ChatMessageSavedEvent(
        Long messageId,
        Long roomId,
        Long senderId,
        String senderNickname,
        String senderProfileImage,
        String content,
        MessageType type,
        LocalDateTime createdAt
) {
}
