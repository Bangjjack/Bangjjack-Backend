package com.project.bangjjack.domain.application.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class InvalidApplicationStatusException extends ApplicationException {

    public InvalidApplicationStatusException() {
        super(ApplicationErrorCode.INVALID_APPLICATION_STATUS);
    }
}
