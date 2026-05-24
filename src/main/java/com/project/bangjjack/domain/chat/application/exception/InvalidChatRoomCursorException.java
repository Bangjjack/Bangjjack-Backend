package com.project.bangjjack.domain.chat.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class InvalidChatRoomCursorException extends ApplicationException {
    public InvalidChatRoomCursorException() {
        super(ChatRoomErrorCode.INVALID_CHAT_ROOM_CURSOR);
    }
}
