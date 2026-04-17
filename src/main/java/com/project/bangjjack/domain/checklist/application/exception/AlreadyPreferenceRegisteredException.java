package com.project.bangjjack.domain.checklist.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class AlreadyPreferenceRegisteredException extends ApplicationException {

    public AlreadyPreferenceRegisteredException() {
        super(ChecklistErrorCode.ALREADY_PREFERENCE_REGISTERED);
    }
}
