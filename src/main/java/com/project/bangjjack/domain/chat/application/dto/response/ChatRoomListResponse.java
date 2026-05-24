package com.project.bangjjack.domain.chat.application.dto.response;

import java.util.List;

public record ChatRoomListResponse(
        List<ChatRoomSummaryResponse> rooms,
        String nextCursor,
        boolean hasNext
) {
    public static ChatRoomListResponse from(List<ChatRoomSummaryResponse> rooms, String nextCursor, boolean hasNext) {
        return new ChatRoomListResponse(rooms, nextCursor, hasNext);
    }
}
