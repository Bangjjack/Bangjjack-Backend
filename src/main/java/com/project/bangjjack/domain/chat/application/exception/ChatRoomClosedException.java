package com.project.bangjjack.domain.chat.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class ChatRoomClosedException extends ApplicationException {

    public ChatRoomClosedException() {
        super(ChatRoomErrorCode.CHAT_ROOM_CLOSED);
    }
}
