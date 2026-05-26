package com.project.bangjjack.domain.chat.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class InvalidWsMessageFormatException extends ApplicationException {
    public InvalidWsMessageFormatException() {
        super(ChatRoomErrorCode.INVALID_WS_MESSAGE_FORMAT);
    }
}
