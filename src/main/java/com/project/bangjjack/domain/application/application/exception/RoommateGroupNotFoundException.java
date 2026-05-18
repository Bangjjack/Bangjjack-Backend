package com.project.bangjjack.domain.application.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class RoommateGroupNotFoundException extends ApplicationException {

    public RoommateGroupNotFoundException() {
        super(ApplicationErrorCode.ROOMMATE_GROUP_NOT_FOUND);
    }
}
