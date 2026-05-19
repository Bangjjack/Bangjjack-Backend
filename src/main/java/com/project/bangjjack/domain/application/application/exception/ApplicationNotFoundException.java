package com.project.bangjjack.domain.application.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class ApplicationNotFoundException extends ApplicationException {

    public ApplicationNotFoundException() {
        super(ApplicationErrorCode.APPLICATION_NOT_FOUND);
    }
}
