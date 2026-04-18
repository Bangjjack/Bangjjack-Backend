package com.project.bangjjack.domain.chat.application.dto.response;

import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;

import java.util.List;

public record ChatRoomResponse(
        Long roomId,
        String roomType,
        boolean isNewRoom,
        List<ParticipantResponse> participants,
        String createdAt
) {
    public static ChatRoomResponse from(ChatRoom chatRoom, boolean isNewRoom) {
        return new ChatRoomResponse(
                chatRoom.getId(),
                chatRoom.getRoomType().name(),
                isNewRoom,
                chatRoom.getParticipants().stream().map(ParticipantResponse::from).toList(),
                chatRoom.getCreatedAt() != null ? chatRoom.getCreatedAt().toString() : null
        );
    }
}
