package com.project.bangjjack.domain.chat.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class AlreadyLeftChatRoomException extends ApplicationException {

    public AlreadyLeftChatRoomException() {
        super(ChatRoomErrorCode.ALREADY_LEFT_CHAT_ROOM);
    }
}
