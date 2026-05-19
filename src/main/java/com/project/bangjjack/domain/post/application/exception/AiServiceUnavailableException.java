package com.project.bangjjack.domain.post.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class AiServiceUnavailableException extends ApplicationException {

    public AiServiceUnavailableException() {
        super(PostErrorCode.AI_SERVICE_UNAVAILABLE);
    }
}
