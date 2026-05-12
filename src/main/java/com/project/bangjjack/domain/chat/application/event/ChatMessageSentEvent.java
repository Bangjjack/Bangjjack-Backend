package com.project.bangjjack.domain.chat.application.event;

import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageBroadcast;

public record ChatMessageSentEvent(Long roomId, ChatMessageBroadcast broadcast) {
}
