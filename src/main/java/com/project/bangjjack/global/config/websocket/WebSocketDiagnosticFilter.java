package com.project.bangjjack.global.config.websocket;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class WebSocketDiagnosticFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (uri.startsWith("/ws/chat")) {
            log.debug("[WS 진단] 요청 수신 - method={}, URI={}, Upgrade={}, Origin={}",
                    request.getMethod(), uri,
                    request.getHeader("Upgrade"),
                    request.getHeader("Origin"));
        }
        filterChain.doFilter(request, response);
    }
}
