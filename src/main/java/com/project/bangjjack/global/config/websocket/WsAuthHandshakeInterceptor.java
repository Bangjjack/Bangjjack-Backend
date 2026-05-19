package com.project.bangjjack.global.config.websocket;

import com.project.bangjjack.domain.auth.application.exception.InvalidWsTokenException;
import com.project.bangjjack.domain.auth.application.usecase.AuthUseCase;
import com.project.bangjjack.global.jwt.principal.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WsAuthHandshakeInterceptor implements HandshakeInterceptor {

    private final AuthUseCase authUseCase;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        List<String> tokens = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams()
                .get("token");

        String wsToken = (tokens != null && !tokens.isEmpty()) ? tokens.get(0) : null;

        try {
            MemberPrincipal principal = authUseCase.authenticateWsToken(wsToken);
            attributes.put("principal", principal);
            log.debug("[WS] 핸드셰이크 인증 성공 - userId={}", principal.getMemberId());
            return true;
        } catch (InvalidWsTokenException e) {
            log.warn("[WS] 핸드셰이크 거부: 유효하지 않은 토큰 - uri={}", request.getURI());
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {}
}
