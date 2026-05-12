package com.project.bangjjack.domain.chat.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bangjjack.domain.chat.application.dto.request.SendChatMessageRequest;
import com.project.bangjjack.domain.chat.application.dto.request.WebSocketInboundMessage;
import com.project.bangjjack.domain.chat.application.dto.response.WebSocketErrorResponse;
import com.project.bangjjack.domain.chat.application.usecase.ChatMessageUseCase;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomGetService;
import com.project.bangjjack.global.common.exception.ApplicationException;
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
    private final ChatRoomGetService chatRoomGetService;
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

        try {
            switch (inbound.type()) {
                case SUBSCRIBE -> {
                    log.debug("[WS] 구독 시도 - userId={}, roomId={}", principal.getMemberId(), inbound.roomId());
                    chatRoomGetService.validateSubscription(inbound.roomId(), principal.getMemberId());
                    
                    sessionStore.subscribe(inbound.roomId(), session);
                    log.debug("[WS] 구독 등록 완료 - userId={}, roomId={}", principal.getMemberId(), inbound.roomId());
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
        } catch (ApplicationException e) {
            log.warn("[WS] 도메인 예외 - userId={}, roomId={}, code={}, 원인={}",
                    principal.getMemberId(), inbound.roomId(), e.getErrorCode().getCode(), e.getMessage());
            sendError(session, WebSocketErrorResponse.from(e));
        } catch (Exception e) {
            log.error("[WS] 예상치 못한 오류 - userId={}, roomId={}, 원인={}",
                    principal.getMemberId(), inbound.roomId(), e.getMessage(), e);
            sendError(session, WebSocketErrorResponse.unknown());
        }
    }

    private void sendError(WebSocketSession session, WebSocketErrorResponse error) {
        if (!session.isOpen()) return;
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(error)));
        } catch (Exception ex) {
            log.warn("[WS] 에러 응답 전송 실패 - sessionId={}, 원인={}", session.getId(), ex.getMessage());
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
