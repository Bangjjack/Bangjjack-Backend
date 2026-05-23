package com.project.bangjjack.domain.chat.application.dto.response;

import java.util.List;

public record ChatRoomListResponse(
        List<ChatRoomSummaryResponse> rooms,
        Long nextCursor,
        boolean hasNext
) {
    public static ChatRoomListResponse of(List<ChatRoomSummaryResponse> rooms, Long nextCursor, boolean hasNext) {
        return new ChatRoomListResponse(rooms, nextCursor, hasNext);
    }
}
