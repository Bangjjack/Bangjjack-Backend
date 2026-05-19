package com.project.bangjjack.domain.application.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class ApplicationPreconditionNotMetException extends ApplicationException {

    public ApplicationPreconditionNotMetException() {
        super(ApplicationErrorCode.APPLICATION_PRECONDITION_NOT_MET);
    }
}
