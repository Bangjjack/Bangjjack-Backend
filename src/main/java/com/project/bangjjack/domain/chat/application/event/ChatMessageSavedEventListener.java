package com.project.bangjjack.domain.chat.application.event;

import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageAckResponse;
import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageResponse;
import com.project.bangjjack.domain.chat.infrastructure.redis.ChatIdempotencyService;
import com.project.bangjjack.domain.chat.infrastructure.redis.ChatRedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChatMessageSavedEventListener {

    private final ChatRedisPublisher chatRedisPublisher;
    private final ChatIdempotencyService chatIdempotencyService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ChatMessageSavedEvent event) {
        chatRedisPublisher.publish(event.roomId(), ChatMessageResponse.from(event));
        chatIdempotencyService.store(
                event.senderId(),
                event.clientTempId(),
                new ChatMessageAckResponse(event.messageId(), event.clientTempId(), event.createdAt())
        );
    }
}
