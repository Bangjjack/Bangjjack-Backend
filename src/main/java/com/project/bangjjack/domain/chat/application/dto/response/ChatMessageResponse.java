package com.project.bangjjack.domain.chat.application.dto.response;

import com.project.bangjjack.domain.chat.domain.entity.Chat;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long messageId,
        Long senderId,
        String content,
        LocalDateTime createdAt
) {
    public static ChatMessageResponse from(Chat chat) {
        return new ChatMessageResponse(
                chat.getId(),
                chat.getSenderId(),
                chat.getContent(),
                chat.getCreatedAt()
        );
    }
}
