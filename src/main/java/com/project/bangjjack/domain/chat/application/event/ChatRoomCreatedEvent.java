package com.project.bangjjack.domain.chat.application.event;

import java.util.List;

public record ChatRoomCreatedEvent(Long roomId, List<Long> participantIds) {
}
