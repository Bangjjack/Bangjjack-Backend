package com.project.bangjjack.domain.chat.presentation.websocket;

import com.project.bangjjack.domain.chat.application.dto.request.SendChatMessageRequest;
import com.project.bangjjack.domain.chat.application.exception.ChatRoomErrorCode;
import com.project.bangjjack.domain.chat.application.usecase.SendChatMessageUseCase;
import com.project.bangjjack.global.common.exception.ApplicationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatMessageMappingController {

    private final SendChatMessageUseCase sendChatMessageUseCase;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat-rooms/{roomId}/messages")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Valid @Payload SendChatMessageRequest request,
            Principal principal
    ) {
        Long currentUserId = Long.parseLong(principal.getName());
        sendChatMessageUseCase.execute(roomId, currentUserId, request);
    }

    @MessageExceptionHandler(ApplicationException.class)
    public void handleApplicationException(ApplicationException e, Principal principal) {
        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                ChatSocketErrorResponse.from(e.getErrorCode())
        );
    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationException(Principal principal) {
        messagingTemplate.convertAndSendToUser(
                principal.getName(),
                "/queue/errors",
                ChatSocketErrorResponse.from(ChatRoomErrorCode.INVALID_CHAT_MESSAGE)
        );
    }
}
