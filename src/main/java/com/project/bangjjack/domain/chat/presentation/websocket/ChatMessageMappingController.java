package com.project.bangjjack.domain.chat.presentation.websocket;

import com.project.bangjjack.domain.chat.application.dto.request.SendChatMessageRequest;
import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageAckResponse;
import com.project.bangjjack.domain.chat.application.usecase.SendChatMessageUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.project.bangjjack.global.config.websocket.UserPrincipal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatMessageMappingController {

    private final SendChatMessageUseCase sendChatMessageUseCase;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat-rooms/{roomId}/messages")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Valid @Payload SendChatMessageRequest request,
            UserPrincipal principal
    ) {
        ChatMessageAckResponse ack = sendChatMessageUseCase.execute(roomId, principal.userId(), request);
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/messages/ack", ack);
    }
}
