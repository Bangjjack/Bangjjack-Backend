package com.project.bangjjack.domain.chat.presentation.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bangjjack.domain.chat.application.exception.ChatRoomErrorCode;
import com.project.bangjjack.global.common.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ChatGlobalExceptionHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @MessageExceptionHandler(ApplicationException.class)
    public void handleApplicationException(ApplicationException e, Message<?> message, Principal principal) {
        if (principal == null) return;

        log.debug("비즈니스 예외 발생 userId={} destination={} errorCode={}",
                principal.getName(), extractDestination(message), e.getErrorCode());

        String clientTempId = extractClientTempId(message);
        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                ChatSocketErrorResponse.of(e.getErrorCode(), clientTempId)
        );
    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationException(Message<?> message, Principal principal) {
        if (principal == null) return;

        log.debug("메시지 유효성 검증 실패 userId={} destination={}", principal.getName(), extractDestination(message));
        String clientTempId = extractClientTempId(message);
        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                ChatSocketErrorResponse.of(ChatRoomErrorCode.INVALID_CHAT_MESSAGE, clientTempId)
        );
    }

    @MessageExceptionHandler(Exception.class)
    public void handleUnexpectedException(Exception e, Message<?> message, Principal principal) {
        String userId = (principal != null) ? principal.getName() : "unknown";
        log.error("STOMP 처리 중 예상치 못한 오류 발생 userId={} destination={}",
                userId, extractDestination(message), e);

        if (principal != null) {
            String clientTempId = extractClientTempId(message);
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/errors",
                    ChatSocketErrorResponse.of(ChatRoomErrorCode.CHAT_SERVER_ERROR, clientTempId)
            );
        }
    }

    // @MessageExceptionHandler의 Message payload는 직렬화 전 byte[]이므로 JSON 파싱으로 추출
    private String extractClientTempId(Message<?> message) {
        if (message == null || message.getPayload() == null) return null;
        if (!(message.getPayload() instanceof byte[] bytes)) return null;
        try {
            JsonNode node = objectMapper.readTree(bytes);
            JsonNode clientTempIdNode = node.get("clientTempId");
            return (clientTempIdNode != null && !clientTempIdNode.isNull()) ? clientTempIdNode.asText() : null;
        } catch (Exception ex) {
            return null;
        }
    }

    private String extractDestination(Message<?> message) {
        if (message == null) return null;
        return StompHeaderAccessor.wrap(message).getDestination();
    }
}
