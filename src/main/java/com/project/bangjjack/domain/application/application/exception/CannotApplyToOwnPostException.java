package com.project.bangjjack.domain.application.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class CannotApplyToOwnPostException extends ApplicationException {

    public CannotApplyToOwnPostException() {
        super(ApplicationErrorCode.CANNOT_APPLY_TO_OWN_POST);
    }
}
