package com.project.bangjjack.domain.chat.infrastructure.broadcaster;

import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageBroadcast;
import com.project.bangjjack.domain.chat.application.dto.response.ReadReceiptBroadcast;
import com.project.bangjjack.global.infrastructure.JsonSerializer;
import com.project.bangjjack.global.infrastructure.websocket.WebSocketSessionStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketBroadcaster implements ChatBroadcaster {

    private final WebSocketSessionStore sessionStore;
    private final JsonSerializer jsonSerializer;

    @Override
    public void broadcastToRoom(Long roomId, ChatMessageBroadcast broadcast) {
        sendToRoom(roomId, jsonSerializer.serialize(broadcast));
        log.debug("[WS Broadcast] 채팅 메시지 브로드캐스트 완료 - roomId={}", roomId);
    }

    @Override
    public void broadcastReadReceipt(Long roomId, ReadReceiptBroadcast broadcast) {
        sendToRoom(roomId, jsonSerializer.serialize(broadcast));
        log.debug("[WS Broadcast] 읽음 알림 브로드캐스트 완료 - roomId={}, readerId={}", roomId, broadcast.readerId());
    }

    private void sendToRoom(Long roomId, String json) {
        Collection<WebSocketSession> sessions = sessionStore.getSessionsByRoom(roomId);
        sessions.forEach(session -> {
            if (!session.isOpen()) return;
            try {
                session.sendMessage(new TextMessage(json));
            } catch (Exception e) {
                log.warn("[WS Broadcast] 전송 실패 - sessionId={}, roomId={}, 원인={}", session.getId(), roomId, e.getMessage());
            }
        });
    }
}
