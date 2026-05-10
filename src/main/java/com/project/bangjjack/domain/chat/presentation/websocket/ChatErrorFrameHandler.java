package com.project.bangjjack.domain.chat.presentation.websocket;

import com.project.bangjjack.domain.chat.application.exception.ChatRoomErrorCode;
import com.project.bangjjack.global.common.exception.ApplicationException;
import com.project.bangjjack.global.common.exception.ErrorCodeInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Slf4j
@Component
public class ChatErrorFrameHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte[]> handleClientMessageProcessingError(
            Message<byte[]> clientMessage, Throwable ex) {

        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        if (cause instanceof ApplicationException appEx) {
            return buildErrorFrame(appEx.getErrorCode());
        }
        log.error("STOMP 처리 중 예상치 못한 오류 발생", cause);
        return buildErrorFrame(ChatRoomErrorCode.CHAT_SERVER_ERROR);
    }

    private Message<byte[]> buildErrorFrame(ErrorCodeInterface errorCode) {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.ERROR);
        headers.setMessage(errorCode.getMessage());
        headers.setNativeHeader("code", String.valueOf(errorCode.getCode()));
        headers.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
    }
}
