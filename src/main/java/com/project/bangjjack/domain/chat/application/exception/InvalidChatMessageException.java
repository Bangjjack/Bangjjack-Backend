package com.project.bangjjack.domain.chat.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class InvalidChatMessageException extends ApplicationException {

    public InvalidChatMessageException() {
        super(ChatRoomErrorCode.INVALID_CHAT_MESSAGE);
    }
}
