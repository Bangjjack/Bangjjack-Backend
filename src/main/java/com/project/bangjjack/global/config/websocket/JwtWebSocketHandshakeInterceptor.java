package com.project.bangjjack.global.config.websocket;

import com.project.bangjjack.global.jwt.JwtAuthenticator;
import com.project.bangjjack.global.jwt.Role;
import com.project.bangjjack.global.jwt.principal.MemberPrincipal;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtWebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtAuthenticator jwtAuthenticator;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            String token = extractToken(request);
            Claims claims = jwtAuthenticator.parseToken(token);
            Long userId = Long.parseLong(claims.getSubject());
            String memberName = claims.get("name", String.class);
            Role role = Role.valueOf(claims.get("role", String.class));

            attributes.put("principal", MemberPrincipal.of(userId, memberName, role));
            log.debug("[WS Handshake] 인증 성공 - userId={}", userId);
            return true;
        } catch (Exception e) {
            log.warn("[WS Handshake] 인증 실패 - 원인={}", e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }

    private String extractToken(ServerHttpRequest request) {
        // Authorization 헤더 우선
        List<String> authHeaders = request.getHeaders().get("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String bearer = authHeaders.get(0);
            if (bearer.startsWith("Bearer ")) {
                return bearer.substring(7);
            }
        }
        // 브라우저 WS API는 커스텀 헤더를 지원하지 않으므로 ?token= 쿼리 파라미터도 허용
        String query = request.getURI().getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    return param.substring(6);
                }
            }
        }
        throw new IllegalArgumentException("WebSocket 인증 토큰이 없습니다.");
    }
}
