package com.project.bangjjack.domain.chat.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bangjjack.domain.chat.application.exception.ChatRoomErrorCode;
import com.project.bangjjack.domain.chat.application.dto.request.SendChatMessageRequest;
import com.project.bangjjack.domain.chat.application.dto.request.WebSocketInboundMessage;
import com.project.bangjjack.domain.chat.application.dto.response.WebSocketErrorResponse;
import com.project.bangjjack.domain.chat.application.exception.NotSubscribedException;
import com.project.bangjjack.domain.chat.application.usecase.ChatMessageUseCase;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomGetService;
import com.project.bangjjack.global.common.exception.ApplicationException;
import com.project.bangjjack.global.infrastructure.redis.WebSocketSessionRegistry;
import com.project.bangjjack.global.infrastructure.websocket.WebSocketSessionStore;
import com.project.bangjjack.global.jwt.principal.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
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
        if (principal == null) {
            log.warn("[WS] 인증 정보 없는 연결 - sessionId={}", session.getId());
            closeSession(session);
            return;
        }

        Long userId = principal.getMemberId();
        List<Long> roomIds = chatRoomGetService.findRoomIdsByUserId(userId);
        sessionStore.registerGlobal(session);
        sessionRegistry.register(userId, session.getId());
        roomIds.forEach(roomId -> sessionStore.subscribe(roomId, session));
        log.debug("[WS] 연결 확립 - userId={}, sessionId={}, 자동 구독 방 수={}", userId, session.getId(), roomIds.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WebSocketInboundMessage inbound;
        try {
            inbound = objectMapper.readValue(message.getPayload(), WebSocketInboundMessage.class);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            log.warn("[WS] 메시지 파싱 오류 - sessionId={}, 원인={}", session.getId(), e.getMessage());
            sendError(session, WebSocketErrorResponse.from(ChatRoomErrorCode.INVALID_WS_MESSAGE_FORMAT));
            return;
        }

        if (inbound.type() == null) {
            log.warn("[WS] 메시지 타입 누락 - sessionId={}", session.getId());
            sendError(session, WebSocketErrorResponse.from(ChatRoomErrorCode.MISSING_MESSAGE_TYPE));
            return;
        }

        MemberPrincipal principal = getPrincipal(session);

        try {
            switch (inbound.type()) {
                case SUBSCRIBE -> {
                    log.debug("[WS] 구독 시도 - userId={}, roomId={}", principal.getMemberId(), inbound.roomId());
                    chatRoomGetService.validateParticipant(inbound.roomId(), principal.getMemberId());
                    sessionStore.subscribe(inbound.roomId(), session);
                    log.debug("[WS] 구독 등록 완료 - userId={}, roomId={}", principal.getMemberId(), inbound.roomId());
                }
                case UNSUBSCRIBE -> {
                    sessionStore.unsubscribe(inbound.roomId(), session);
                    log.debug("[WS] 구독 해제 - userId={}, roomId={}", principal.getMemberId(), inbound.roomId());
                }
                case SEND -> {
                    log.debug("[WS] 메시지 송신 시도 - userId={}, roomId={}", principal.getMemberId(), inbound.roomId());
                    if (!sessionStore.isSubscribed(inbound.roomId(), session)) {
                        throw new NotSubscribedException();
                    }
                    chatMessageUseCase.sendMessage(principal.getMemberId(), inbound.roomId(), new SendChatMessageRequest(inbound.content()));
                }
                default -> log.warn("[WS] 알 수 없는 메시지 타입 - type={}", inbound.type());
            }
        } catch (ApplicationException e) {
            log.warn("[WS] 도메인 예외 - userId={}, code={}, 원인={}",
                    principal.getMemberId(), e.getErrorCode().getCode(), e.getMessage());
            sendError(session, WebSocketErrorResponse.from(e));
        } catch (Exception e) {
            log.error("[WS] 예상치 못한 오류 - userId={}, 원인={}",
                    principal.getMemberId(), e.getMessage(), e);
            sendError(session, WebSocketErrorResponse.from(ChatRoomErrorCode.WS_INTERNAL_ERROR));
        }
    }

    private void sendError(WebSocketSession session, WebSocketErrorResponse error) {
        if (!session.isOpen()) {
            return;
        }
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(error)));
        } catch (Exception ex) {
            log.warn("[WS] 에러 응답 전송 실패 - sessionId={}, 원인={}", session.getId(), ex.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        MemberPrincipal principal = getPrincipal(session);
        if (principal == null) {
            return;
        }
        sessionStore.removeAll(session);
        sessionStore.deregisterGlobal(session);
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

    private void closeSession(WebSocketSession session) {
        try {
            session.close(CloseStatus.POLICY_VIOLATION);
        } catch (Exception e) {
            log.warn("[WS] 세션 강제 종료 실패 - sessionId={}", session.getId());
        }
    }
}
