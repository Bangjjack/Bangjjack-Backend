package com.project.bangjjack.domain.post.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class PreferenceNotRegisteredException extends ApplicationException {

    public PreferenceNotRegisteredException() {
        super(PostErrorCode.PREFERENCE_NOT_REGISTERED);
    }
}
