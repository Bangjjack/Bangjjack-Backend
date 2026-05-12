package com.project.bangjjack.domain.chat.application.dto.response;

import com.project.bangjjack.global.common.exception.ApplicationException;

public record WebSocketErrorResponse(
        String type,
        int code,
        String message
) {
    public static WebSocketErrorResponse from(ApplicationException e) {
        return new WebSocketErrorResponse("ERROR", e.getErrorCode().getCode(), e.getMessage());
    }

    public static WebSocketErrorResponse unknown() {
        return new WebSocketErrorResponse("ERROR", 500, "서버 내부 오류가 발생했습니다.");
    }
}
