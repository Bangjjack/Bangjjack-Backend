package com.project.bangjjack.domain.chat.application.dto.response;

import com.project.bangjjack.domain.chat.domain.entity.Chat;
import com.project.bangjjack.domain.chat.domain.entity.MessageType;

import java.time.LocalDateTime;

public record ChatMessageBroadcast(
        Long messageId,
        Long roomId,
        Long senderId,
        String content,
        MessageType messageType,
        LocalDateTime createdAt
) {
    public static ChatMessageBroadcast from(Chat chat, Long roomId) {
        return new ChatMessageBroadcast(
                chat.getId(),
                roomId,
                chat.getSenderId(),
                chat.getContent(),
                chat.getMessageType(),
                chat.getCreatedAt()
        );
    }
}
