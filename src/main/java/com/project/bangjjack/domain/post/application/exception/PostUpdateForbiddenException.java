package com.project.bangjjack.domain.post.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class PostUpdateForbiddenException extends ApplicationException {

    public PostUpdateForbiddenException() {
        super(PostErrorCode.POST_UPDATE_FORBIDDEN);
    }
}
