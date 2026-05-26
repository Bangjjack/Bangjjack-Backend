package com.project.bangjjack.domain.application.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class NoOpenPostForUserException extends ApplicationException {

    public NoOpenPostForUserException() {
        super(ApplicationErrorCode.NO_OPEN_POST_FOR_USER);
    }
}
