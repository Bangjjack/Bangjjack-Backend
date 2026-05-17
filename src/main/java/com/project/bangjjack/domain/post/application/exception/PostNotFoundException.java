package com.project.bangjjack.domain.post.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class PostNotFoundException extends ApplicationException {

    public PostNotFoundException() {
        super(PostErrorCode.POST_NOT_FOUND);
    }
}
