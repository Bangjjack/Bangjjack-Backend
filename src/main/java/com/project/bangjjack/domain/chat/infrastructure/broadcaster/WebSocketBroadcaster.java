package com.project.bangjjack.domain.chat.infrastructure.broadcaster;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageBroadcast;
import com.project.bangjjack.global.infrastructure.websocket.WebSocketSessionStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketBroadcaster implements ChatBroadcaster {

    private final WebSocketSessionStore sessionStore;
    private final ObjectMapper objectMapper;

    @Override
    public void broadcastToRoom(Long roomId, ChatMessageBroadcast broadcast) {
        String json = serialize(broadcast);
        Set<WebSocketSession> sessions = sessionStore.getSessionsByRoom(roomId);

        sessions.forEach(session -> {
            if (!session.isOpen()) return;
            try {
                session.sendMessage(new TextMessage(json));
            } catch (Exception e) {
                log.warn("[WS Broadcast] 전송 실패 - sessionId={}, roomId={}, 원인={}", session.getId(), roomId, e.getMessage());
            }
        });
        log.debug("[WS Broadcast] 브로드캐스트 완료 - roomId={}, recipients={}", roomId, sessions.size());
    }

    private String serialize(ChatMessageBroadcast broadcast) {
        try {
            return objectMapper.writeValueAsString(broadcast);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("ChatMessageBroadcast 직렬화 실패", e);
        }
    }
}
