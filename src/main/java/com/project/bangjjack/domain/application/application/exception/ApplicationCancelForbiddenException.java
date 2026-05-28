package com.project.bangjjack.domain.application.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class ApplicationCancelForbiddenException extends ApplicationException {

    public ApplicationCancelForbiddenException() {
        super(ApplicationErrorCode.APPLICATION_CANCEL_FORBIDDEN);
    }
}
