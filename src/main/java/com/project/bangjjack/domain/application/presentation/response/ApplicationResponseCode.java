package com.project.bangjjack.domain.application.presentation.response;

import com.project.bangjjack.global.common.response.ResponseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApplicationResponseCode implements ResponseCodeInterface {

    APPLICATION_CREATED(201, HttpStatus.CREATED, "룸메이트 신청이 생성되었습니다."),
    APPLICATION_PROCESSED(200, HttpStatus.OK, "룸메이트 신청이 처리되었습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
