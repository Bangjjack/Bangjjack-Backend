package com.project.bangjjack.domain.chat.application.dto.response;

import com.project.bangjjack.domain.chat.domain.entity.Chat;

import java.time.LocalDateTime;

public record ChatMessageBroadcast(
        Long messageId,
        Long roomId,
        Long senderId,
        String content,
        LocalDateTime createdAt
) {
    public static ChatMessageBroadcast from(Chat chat, Long roomId) {
        return new ChatMessageBroadcast(
                chat.getId(),
                roomId,
                chat.getSenderId(),
                chat.getContent(),
                chat.getCreatedAt()
        );
    }
}
