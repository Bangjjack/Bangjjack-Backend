package com.project.bangjjack.global.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        Exception exception = (Exception) request.getAttribute("exception");

        String code;
        String message;

        if (exception instanceof ExpiredJwtException) {
            code = "TOKEN_EXPIRED";
            message = "액세스 토큰이 만료되었습니다.";
        } else if (exception instanceof MalformedJwtException) {
            code = "INVALID_TOKEN";
            message = "유효하지 않은 토큰 형식입니다.";
        } else if (exception instanceof SignatureException) {
            code = "INVALID_SIGNATURE";
            message = "토큰 서명이 유효하지 않습니다.";
        } else {
            code = "UNAUTHORIZED";
            message = "인증이 필요합니다.";
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        response.getWriter().write(
                objectMapper.writeValueAsString(Map.of("code", code, "message", message))
        );
    }
}
