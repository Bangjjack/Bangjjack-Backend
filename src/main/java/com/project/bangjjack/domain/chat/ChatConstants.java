package com.project.bangjjack.domain.chat;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatConstants {
    public static final String CHAT_ROOM_SUBSCRIBE_PREFIX = "/sub/chat-rooms/";
    public static final String CHAT_ROOM_TOPIC_PREFIX = "chat.room.";
    public static final String IDEMPOTENCY_KEY_PREFIX = "chat.idempotency.";
    public static final long IDEMPOTENCY_TTL_MINUTES = 5;
}
