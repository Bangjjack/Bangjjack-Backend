package com.project.bangjjack.domain.post.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class AlreadyOpenPostExistsException extends ApplicationException {

    public AlreadyOpenPostExistsException() {
        super(PostErrorCode.ALREADY_OPEN_POST_EXISTS);
    }
}
