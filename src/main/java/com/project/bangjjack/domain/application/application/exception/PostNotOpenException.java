package com.project.bangjjack.domain.application.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class PostNotOpenException extends ApplicationException {

    public PostNotOpenException() {
        super(ApplicationErrorCode.POST_NOT_OPEN);
    }
}
