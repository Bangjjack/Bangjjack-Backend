package com.project.bangjjack.domain.auth.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class UnauthorizedEmailDomainException extends ApplicationException {

    public UnauthorizedEmailDomainException() {
        super(AuthErrorCode.UNAUTHORIZED_EMAIL_DOMAIN);
    }
}
