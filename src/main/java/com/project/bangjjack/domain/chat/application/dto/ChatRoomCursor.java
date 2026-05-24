package com.project.bangjjack.domain.chat.application.dto;

import com.project.bangjjack.domain.chat.application.exception.InvalidChatRoomCursorException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

public record ChatRoomCursor(Long roomId, LocalDateTime lastMessageAt) {
    public static ChatRoomCursor decode(String encoded) {
        try {
            String raw = new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
            int sep = raw.indexOf(':');
            Long roomId = Long.parseLong(raw.substring(0, sep));
            String timeStr = raw.substring(sep + 1);
            LocalDateTime lastMessageAt = timeStr.isEmpty() ? null : LocalDateTime.parse(timeStr);
            return new ChatRoomCursor(roomId, lastMessageAt);
        } catch (Exception e) {
            throw new InvalidChatRoomCursorException();
        }
    }

    public String encode() {
        String timeStr = lastMessageAt != null ? lastMessageAt.toString() : "";
        String raw = roomId + ":" + timeStr;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }
}
