package com.project.bangjjack.domain.chat.infrastructure;

import com.project.bangjjack.domain.chat.application.event.ChatMessageSentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChatMessageEventListener {

    private final ChatMessagePublisher chatMessagePublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ChatMessageSentEvent event) {
        chatMessagePublisher.publish(event.roomId(), event.broadcast());
    }
}
