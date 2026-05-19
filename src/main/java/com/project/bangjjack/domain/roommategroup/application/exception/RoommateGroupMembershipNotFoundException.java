package com.project.bangjjack.domain.roommategroup.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class RoommateGroupMembershipNotFoundException extends ApplicationException {

    public RoommateGroupMembershipNotFoundException() {
        super(RoommateGroupErrorCode.ROOMMATE_GROUP_MEMBERSHIP_NOT_FOUND);
    }
}
