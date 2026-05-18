package com.project.bangjjack.domain.application.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class InvalidTargetStatusException extends ApplicationException {

    public InvalidTargetStatusException() {
        super(ApplicationErrorCode.INVALID_TARGET_STATUS);
    }
}
