package com.project.bangjjack.domain.post.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class PostNotEditableException extends ApplicationException {

    public PostNotEditableException() {
        super(PostErrorCode.POST_NOT_EDITABLE);
    }
}
