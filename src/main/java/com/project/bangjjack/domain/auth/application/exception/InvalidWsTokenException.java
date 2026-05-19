package com.project.bangjjack.domain.auth.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class InvalidWsTokenException extends ApplicationException {

    public InvalidWsTokenException() {
        super(AuthErrorCode.INVALID_WS_TOKEN);
    }
}
