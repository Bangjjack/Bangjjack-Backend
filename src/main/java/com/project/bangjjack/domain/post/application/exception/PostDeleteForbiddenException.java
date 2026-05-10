package com.project.bangjjack.domain.post.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class PostDeleteForbiddenException extends ApplicationException {

    public PostDeleteForbiddenException() {
        super(PostErrorCode.POST_DELETE_FORBIDDEN);
    }
}
