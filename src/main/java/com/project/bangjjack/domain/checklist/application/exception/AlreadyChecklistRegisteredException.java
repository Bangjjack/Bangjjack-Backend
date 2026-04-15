package com.project.bangjjack.domain.checklist.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class AlreadyChecklistRegisteredException extends ApplicationException {

    public AlreadyChecklistRegisteredException() {
        super(ChecklistErrorCode.ALREADY_CHECKLIST_REGISTERED);
    }
}
