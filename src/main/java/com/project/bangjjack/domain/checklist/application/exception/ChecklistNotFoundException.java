package com.project.bangjjack.domain.checklist.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class ChecklistNotFoundException extends ApplicationException {

    public ChecklistNotFoundException() {
        super(ChecklistErrorCode.CHECKLIST_NOT_FOUND);
    }
}
