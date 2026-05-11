package com.project.bangjjack.domain.chat.presentation;

import com.project.bangjjack.domain.chat.application.dto.request.SendChatMessageRequest;
import com.project.bangjjack.domain.chat.application.usecase.ChatMessageUseCase;
import com.project.bangjjack.global.jwt.principal.MemberPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageUseCase chatMessageUseCase;

    @MessageMapping("/chat/rooms/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload @Valid SendChatMessageRequest request,
            Principal principal) {
        MemberPrincipal memberPrincipal = (MemberPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        chatMessageUseCase.sendMessage(memberPrincipal.getMemberId(), roomId, request);
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public String handleException(Exception e) {
        log.warn("STOMP 메시지 처리 중 오류 발생: {}", e.getMessage());
        return e.getMessage();
    }
}
