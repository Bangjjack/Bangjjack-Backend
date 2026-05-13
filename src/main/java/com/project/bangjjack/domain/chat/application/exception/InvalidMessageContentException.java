package com.project.bangjjack.domain.chat.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class InvalidMessageContentException extends ApplicationException {
    public InvalidMessageContentException() {
        super(ChatRoomErrorCode.INVALID_MESSAGE_CONTENT);
    }
}
