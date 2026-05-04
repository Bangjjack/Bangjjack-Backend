package com.project.bangjjack.domain.chat.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class ChatRoomNotFoundException extends ApplicationException {

    public ChatRoomNotFoundException() {
        super(ChatRoomErrorCode.CHAT_ROOM_NOT_FOUND);
    }
}
