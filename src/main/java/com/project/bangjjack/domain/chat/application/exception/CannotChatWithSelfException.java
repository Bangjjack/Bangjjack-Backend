package com.project.bangjjack.domain.chat.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class CannotChatWithSelfException extends ApplicationException {

    public CannotChatWithSelfException() {
        super(ChatRoomErrorCode.CANNOT_CHAT_WITH_SELF);
    }
}
