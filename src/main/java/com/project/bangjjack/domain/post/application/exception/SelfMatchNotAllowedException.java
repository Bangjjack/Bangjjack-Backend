package com.project.bangjjack.domain.post.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class SelfMatchNotAllowedException extends ApplicationException {

    public SelfMatchNotAllowedException() {
        super(PostErrorCode.SELF_MATCH_NOT_ALLOWED);
    }
}
