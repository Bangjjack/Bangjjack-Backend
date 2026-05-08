package com.project.bangjjack.domain.chat.application.dto.response;

import com.project.bangjjack.domain.chat.application.event.ChatMessageSavedEvent;
import com.project.bangjjack.domain.chat.domain.entity.MessageType;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long messageId,
        Long roomId,
        Long senderId,
        String senderNickname,
        String senderProfileImage,
        String content,
        MessageType type,
        LocalDateTime createdAt
) {
    public static ChatMessageResponse from(ChatMessageSavedEvent event) {
        return new ChatMessageResponse(
                event.messageId(),
                event.roomId(),
                event.senderId(),
                event.senderNickname(),
                event.senderProfileImage(),
                event.content(),
                event.type(),
                event.createdAt()
        );
    }
}
