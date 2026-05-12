package com.project.bangjjack.domain.chat.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bangjjack.domain.chat.application.dto.request.SendChatMessageRequest;
import com.project.bangjjack.domain.chat.application.dto.request.WebSocketInboundMessage;
import com.project.bangjjack.domain.chat.application.usecase.ChatMessageUseCase;
import com.project.bangjjack.global.infrastructure.redis.WebSocketSessionRegistry;
import com.project.bangjjack.global.infrastructure.websocket.WebSocketSessionStore;
import com.project.bangjjack.global.jwt.principal.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatMessageUseCase chatMessageUseCase;
    private final WebSocketSessionStore sessionStore;
    private final WebSocketSessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        MemberPrincipal principal = getPrincipal(session);
        sessionRegistry.register(principal.getMemberId(), session.getId());
        log.debug("[WS] 연결 확립 - userId={}, sessionId={}", principal.getMemberId(), session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WebSocketInboundMessage inbound = objectMapper.readValue(message.getPayload(), WebSocketInboundMessage.class);
        MemberPrincipal principal = getPrincipal(session);

        switch (inbound.type()) {
            case SUBSCRIBE -> {
                sessionStore.subscribe(inbound.roomId(), session);
                log.debug("[WS] 구독 등록 - userId={}, roomId={}", principal.getMemberId(), inbound.roomId());
            }
            case UNSUBSCRIBE -> {
                sessionStore.unsubscribe(inbound.roomId(), session);
                log.debug("[WS] 구독 해제 - userId={}, roomId={}", principal.getMemberId(), inbound.roomId());
            }
            case SEND -> {
                log.debug("[WS] 메시지 수신 - userId={}, roomId={}", principal.getMemberId(), inbound.roomId());
                chatMessageUseCase.sendMessage(
                        principal.getMemberId(),
                        inbound.roomId(),
                        new SendChatMessageRequest(inbound.content())
                );
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        MemberPrincipal principal = getPrincipal(session);
        if (principal == null) return;
        sessionStore.removeAll(session);
        sessionRegistry.remove(principal.getMemberId(), session.getId());
        log.debug("[WS] 연결 해제 - userId={}, sessionId={}, status={}", principal.getMemberId(), session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.warn("[WS] 전송 오류 - sessionId={}, 원인={}", session.getId(), exception.getMessage());
    }

    private MemberPrincipal getPrincipal(WebSocketSession session) {
        return (MemberPrincipal) session.getAttributes().get("principal");
    }
}
