package com.project.bangjjack.domain.auth.application.exception;

import com.project.bangjjack.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCodeInterface {

    INVALID_AUTHORIZATION_CODE(40101, HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 인증 코드입니다."),
    INVALID_WS_TOKEN(40102, HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 WebSocket 토큰입니다."),
    UNAUTHORIZED_EMAIL_DOMAIN(40103, HttpStatus.UNAUTHORIZED, "가천대학교 이메일(@gachon.ac.kr)로만 로그인 가능합니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
