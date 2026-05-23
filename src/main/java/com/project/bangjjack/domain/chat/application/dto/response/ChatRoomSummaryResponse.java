package com.project.bangjjack.domain.chat.application.dto.response;

import com.project.bangjjack.domain.chat.domain.entity.Chat;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.user.domain.entity.User;

import java.time.LocalDateTime;

public record ChatRoomSummaryResponse(
        Long roomId,
        Long partnerId,
        String partnerName,
        String partnerProfileImage,
        String lastMessage,
        LocalDateTime lastMessageAt,
        long unreadCount
) {
    public static ChatRoomSummaryResponse from(ChatRoom room, User partner, Chat lastChat, long unreadCount) {
        return new ChatRoomSummaryResponse(
                room.getId(),
                partner != null ? partner.getId() : null,
                partner != null ? partner.getUsername() : null,
                partner != null ? partner.getProfileImage() : null,
                lastChat != null ? lastChat.getContent() : null,
                lastChat != null ? lastChat.getCreatedAt() : null,
                unreadCount
        );
    }
}
