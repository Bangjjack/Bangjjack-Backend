package com.project.bangjjack.domain.user.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class NotOnboardedException extends ApplicationException {

    public NotOnboardedException() {
        super(UserErrorCode.NOT_ONBOARDED);
    }
}
