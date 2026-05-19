package com.project.bangjjack.domain.application.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class ApplicationProcessForbiddenException extends ApplicationException {

    public ApplicationProcessForbiddenException() {
        super(ApplicationErrorCode.APPLICATION_PROCESS_FORBIDDEN);
    }
}
