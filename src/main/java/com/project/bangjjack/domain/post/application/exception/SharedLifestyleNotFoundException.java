package com.project.bangjjack.domain.post.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class SharedLifestyleNotFoundException extends ApplicationException {

    public SharedLifestyleNotFoundException() {
        super(PostErrorCode.SHARED_LIFESTYLE_NOT_FOUND);
    }
}
