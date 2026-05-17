package com.project.bangjjack.domain.post.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class ChecklistNotRegisteredException extends ApplicationException {

    public ChecklistNotRegisteredException() {
        super(PostErrorCode.CHECKLIST_NOT_REGISTERED);
    }
}
