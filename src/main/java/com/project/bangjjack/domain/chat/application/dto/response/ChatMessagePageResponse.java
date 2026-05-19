package com.project.bangjjack.domain.chat.application.dto.response;

import java.util.List;

public record ChatMessagePageResponse(
        List<ChatMessageResponse> messages,
        Long nextCursor,
        boolean hasNext
) {
    public static ChatMessagePageResponse of(List<ChatMessageResponse> messages, Long nextCursor, boolean hasNext) {
        return new ChatMessagePageResponse(messages, nextCursor, hasNext);
    }
}
