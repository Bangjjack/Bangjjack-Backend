package com.project.bangjjack.domain.user.application.exception;

import com.project.bangjjack.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCodeInterface {

    USER_NOT_FOUND(40101, HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
