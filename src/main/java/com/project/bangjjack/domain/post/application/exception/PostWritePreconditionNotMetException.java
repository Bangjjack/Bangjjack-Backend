package com.project.bangjjack.domain.post.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class PostWritePreconditionNotMetException extends ApplicationException {

    public PostWritePreconditionNotMetException() {
        super(PostErrorCode.POST_WRITE_PRECONDITION_NOT_MET);
    }
}
