package com.project.bangjjack.domain.auth.presentation.response;

import com.project.bangjjack.global.common.response.ResponseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthResponseCode implements ResponseCodeInterface {

    TOKEN_EXCHANGED(200, HttpStatus.OK, "토큰이 발급되었습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
