package com.project.bangjjack.domain.user.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class AlreadyOnboardedException extends ApplicationException {

    public AlreadyOnboardedException() {
        super(UserErrorCode.ALREADY_ONBOARDED);
    }
}
