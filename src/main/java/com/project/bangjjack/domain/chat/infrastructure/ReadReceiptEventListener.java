package com.project.bangjjack.domain.chat.infrastructure;

import com.project.bangjjack.domain.chat.application.dto.response.ReadReceiptBroadcast;
import com.project.bangjjack.domain.chat.application.event.ReadReceiptEvent;
import com.project.bangjjack.domain.chat.infrastructure.broadcaster.ChatBroadcaster;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReadReceiptEventListener {

    private final ChatBroadcaster chatBroadcaster;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReadReceiptEvent event) {
        ReadReceiptBroadcast broadcast = ReadReceiptBroadcast.of(event.roomId(), event.readerId(), event.lastReadMessageId());
        chatBroadcaster.broadcastReadReceipt(event.roomId(), broadcast);
    }
}
