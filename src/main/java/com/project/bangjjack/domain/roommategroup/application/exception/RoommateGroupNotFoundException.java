package com.project.bangjjack.domain.roommategroup.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class RoommateGroupNotFoundException extends ApplicationException {

    public RoommateGroupNotFoundException() {
        super(RoommateGroupErrorCode.ROOMMATE_GROUP_NOT_FOUND);
    }
}
