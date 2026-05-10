package com.project.bangjjack.domain.chat.presentation.websocket;

import com.project.bangjjack.global.common.exception.ErrorCodeInterface;

public record ChatSocketErrorResponse(int code, String message, String clientTempId) {

    public static ChatSocketErrorResponse of(ErrorCodeInterface errorCode, String clientTempId) {
        return new ChatSocketErrorResponse(errorCode.getCode(), errorCode.getMessage(), clientTempId);
    }
}
