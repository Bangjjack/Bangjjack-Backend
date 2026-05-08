package com.project.bangjjack.domain.chat.presentation.websocket;

import com.project.bangjjack.domain.chat.ChatConstants;
import com.project.bangjjack.domain.chat.application.exception.ChatForbiddenException;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomGetService;
import com.project.bangjjack.global.config.websocket.UserPrincipal;
import com.project.bangjjack.global.jwt.JwtAuthenticator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtAuthenticator jwtAuthenticator;
    private final ChatRoomGetService chatRoomGetService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        switch (accessor.getCommand()) {
            case CONNECT -> handleConnect(accessor, message);
            case SUBSCRIBE -> handleSubscribe(accessor, message);
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor, Message<?> message) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new MessageDeliveryException(message, "Missing or invalid Authorization header");
        }
        Claims claims = jwtAuthenticator.parseToken(authHeader.substring(7));
        Long userId = Long.parseLong(claims.getSubject());
        accessor.setUser(new UserPrincipal(userId));
    }

    private void handleSubscribe(StompHeaderAccessor accessor, Message<?> message) {
        Long roomId = extractChatRoomId(accessor.getDestination());
        if (roomId == null) {
            return;
        }

        Principal user = accessor.getUser();
        if (user == null) {
            throw new MessageDeliveryException(message, "Missing or invalid Authorization header");
        }

        Long userId = Long.parseLong(user.getName());
        chatRoomGetService.getById(roomId); // 존재하지 않으면 ChatRoomNotFoundException
        if (!chatRoomGetService.isParticipant(roomId, userId)) {
            throw new ChatForbiddenException();
        }
    }

    private Long extractChatRoomId(String destination) {
        if (destination == null) return null;
        String prefix = ChatConstants.CHAT_ROOM_SUBSCRIBE_PREFIX;
        if (!destination.startsWith(prefix)) return null;
        try {
            return Long.parseLong(destination.substring(prefix.length()));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
