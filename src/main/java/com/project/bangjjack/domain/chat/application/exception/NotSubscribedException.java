package com.project.bangjjack.domain.chat.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class NotSubscribedException extends ApplicationException {
    public NotSubscribedException() {
        super(ChatRoomErrorCode.NOT_SUBSCRIBED);
    }
}
