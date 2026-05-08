package com.project.bangjjack.domain.chat.presentation.websocket;

import com.project.bangjjack.global.common.exception.ErrorCodeInterface;

public record ChatSocketErrorResponse(int code, String message) {

    public static ChatSocketErrorResponse from(ErrorCodeInterface errorCode) {
        return new ChatSocketErrorResponse(errorCode.getCode(), errorCode.getMessage());
    }
}
