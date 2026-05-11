package com.project.bangjjack.domain.chat.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class NotChatParticipantException extends ApplicationException {

    public NotChatParticipantException() {
        super(ChatRoomErrorCode.NOT_CHAT_PARTICIPANT);
    }
}
