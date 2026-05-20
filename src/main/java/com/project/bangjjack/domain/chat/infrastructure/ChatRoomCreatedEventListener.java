package com.project.bangjjack.domain.chat.infrastructure;

import com.project.bangjjack.domain.chat.application.event.ChatRoomCreatedEvent;
import com.project.bangjjack.global.infrastructure.redis.WebSocketSessionRegistry;
import com.project.bangjjack.global.infrastructure.websocket.WebSocketSessionStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomCreatedEventListener {

    private final WebSocketSessionRegistry sessionRegistry;
    private final WebSocketSessionStore sessionStore;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ChatRoomCreatedEvent event) {
        Long roomId = event.roomId();
        for (Long userId : event.participantIds()) {
            Set<String> sessionIds = sessionRegistry.getSessionIds(userId);
            for (String sessionId : sessionIds) {
                sessionStore.getById(sessionId).ifPresentOrElse(
                        session -> {
                            sessionStore.subscribe(roomId, session);
                            log.debug("[WS] 신규 채팅방 동적 구독 - userId={}, roomId={}, sessionId={}", userId, roomId, sessionId);
                        },
                        () -> log.debug("[WS] 세션 미활성 - userId={}, sessionId={}", userId, sessionId)
                );
            }
        }
    }
}
