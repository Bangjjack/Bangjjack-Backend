package com.project.bangjjack.domain.user.application.exception;

import com.project.bangjjack.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCodeInterface {

    ALREADY_ONBOARDED(40201, HttpStatus.CONFLICT, "이미 온보딩이 완료된 사용자입니다."),
    INVALID_BIRTH_YEAR(40202, HttpStatus.BAD_REQUEST, "유효하지 않은 출생년도입니다."),
    USER_NOT_FOUND(40203, HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
