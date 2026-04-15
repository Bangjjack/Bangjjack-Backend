package com.project.bangjjack.domain.checklist.presentation.response;

import com.project.bangjjack.global.common.response.ResponseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChecklistResponseCode implements ResponseCodeInterface {

    CHECKLIST_REGISTERED(201, HttpStatus.CREATED, "생활습관 체크리스트가 등록되었습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
