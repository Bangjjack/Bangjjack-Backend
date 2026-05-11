package com.project.bangjjack.global.config.websocket;

import com.project.bangjjack.global.infrastructure.redis.WebSocketSessionRegistry;
import com.project.bangjjack.global.jwt.JwtAuthenticator;
import com.project.bangjjack.global.jwt.Role;
import com.project.bangjjack.global.jwt.principal.MemberPrincipal;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private final JwtAuthenticator jwtAuthenticator;
    private final WebSocketSessionRegistry sessionRegistry;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = extractToken(accessor);
            Claims claims = jwtAuthenticator.parseToken(token);
            Long userId = Long.parseLong(claims.getSubject());
            String memberName = claims.get("name", String.class);
            Role role = Role.valueOf(claims.get("role", String.class));

            // Last-Connection-Wins: 기존 세션을 새 세션으로 덮어쓴다
            sessionRegistry.register(userId, accessor.getSessionId());
            MemberPrincipal memberPrincipal = MemberPrincipal.of(userId, memberName, role);
            accessor.setUser(new UsernamePasswordAuthenticationToken(
                    memberPrincipal, null, memberPrincipal.getAuthorities()));

            log.debug("WebSocket CONNECT. userId={}, sessionId={}", userId, accessor.getSessionId());
        }

        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            MemberPrincipal principal = (MemberPrincipal) accessor.getUser();
            if (principal != null) {
                sessionRegistry.remove(principal.getMemberId(), accessor.getSessionId());
                log.debug("WebSocket DISCONNECT. userId={}, sessionId={}", principal.getMemberId(), accessor.getSessionId());
            }
        }

        return message;
    }

    private String extractToken(StompHeaderAccessor accessor) {
        String bearerToken = accessor.getFirstNativeHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new IllegalArgumentException("WebSocket 인증 토큰이 없습니다.");
    }
}
