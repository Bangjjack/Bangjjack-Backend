package com.project.bangjjack.domain.auth.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class InvalidAuthorizationCodeException extends ApplicationException {

    public InvalidAuthorizationCodeException() {
        super(AuthErrorCode.INVALID_AUTHORIZATION_CODE);
    }
}
