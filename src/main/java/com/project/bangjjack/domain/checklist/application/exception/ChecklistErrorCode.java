package com.project.bangjjack.domain.checklist.application.exception;

import com.project.bangjjack.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChecklistErrorCode implements ErrorCodeInterface {

    ALREADY_CHECKLIST_REGISTERED(40501, HttpStatus.CONFLICT, "이미 생활습관 체크리스트를 등록한 사용자입니다."),
    CHECKLIST_NOT_FOUND(40502, HttpStatus.NOT_FOUND, "생활습관 체크리스트를 찾을 수 없습니다."),
    ALREADY_PREFERENCE_REGISTERED(40503, HttpStatus.CONFLICT, "이미 룸메이트 선호도를 등록한 사용자입니다."),
    DUPLICATE_PREFERENCE_FACTOR(40504, HttpStatus.BAD_REQUEST, "중복된 선호도 항목이 포함되어 있습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
