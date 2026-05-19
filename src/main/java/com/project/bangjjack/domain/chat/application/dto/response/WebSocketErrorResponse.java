package com.project.bangjjack.domain.chat.application.dto.response;

import com.project.bangjjack.global.common.exception.ApplicationException;
import com.project.bangjjack.global.common.exception.ErrorCodeInterface;

public record WebSocketErrorResponse(
        String type,
        int code,
        String message
) {
    public static WebSocketErrorResponse from(ApplicationException e) {
        return new WebSocketErrorResponse("ERROR", e.getErrorCode().getCode(), e.getMessage());
    }

    public static WebSocketErrorResponse from(ErrorCodeInterface errorCode) {
        return new WebSocketErrorResponse("ERROR", errorCode.getCode(), errorCode.getMessage());
    }
}
