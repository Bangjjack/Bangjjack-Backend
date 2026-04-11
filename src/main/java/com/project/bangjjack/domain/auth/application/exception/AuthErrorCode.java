package com.project.bangjjack.domain.auth.application.exception;

import com.project.bangjjack.global.common.exception.ErrorCodeInterface;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCodeInterface {

    INVALID_AUTHORIZATION_CODE(40801, HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 인증 코드입니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
