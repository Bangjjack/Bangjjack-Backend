package com.project.bangjjack.domain.chat.application.dto.response;

import com.project.bangjjack.domain.chat.domain.entity.ChatRoomParticipant;

public record ParticipantResponse(Long userId) {

    public static ParticipantResponse from(ChatRoomParticipant participant) {
        return new ParticipantResponse(participant.getUserId());
    }
}
