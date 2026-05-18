package com.project.bangjjack.domain.application.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class AlreadyAppliedPendingException extends ApplicationException {

    public AlreadyAppliedPendingException() {
        super(ApplicationErrorCode.ALREADY_APPLIED_PENDING);
    }
}
