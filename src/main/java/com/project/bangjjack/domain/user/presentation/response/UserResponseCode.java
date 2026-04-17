package com.project.bangjjack.domain.user.presentation.response;

import com.project.bangjjack.global.common.response.ResponseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserResponseCode implements ResponseCodeInterface {

    ONBOARDING_COMPLETED(200, HttpStatus.OK, "온보딩이 완료되었습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
