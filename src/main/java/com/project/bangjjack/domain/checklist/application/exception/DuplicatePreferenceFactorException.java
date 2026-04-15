package com.project.bangjjack.domain.checklist.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class DuplicatePreferenceFactorException extends ApplicationException {

    public DuplicatePreferenceFactorException() {
        super(ChecklistErrorCode.DUPLICATE_PREFERENCE_FACTOR);
    }
}
