package com.project.bangjjack.domain.chat.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class ChatForbiddenException extends ApplicationException {

    public ChatForbiddenException() {
        super(ChatRoomErrorCode.CHAT_ROOM_FORBIDDEN);
    }
}
