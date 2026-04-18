package com.project.bangjjack.domain.chat.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateChatRoomRequest(
        @NotNull(message = "상대방 유저 ID는 필수입니다.") Long targetUserId
) {
}
