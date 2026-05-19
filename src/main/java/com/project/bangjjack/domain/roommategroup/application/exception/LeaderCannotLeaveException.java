package com.project.bangjjack.domain.roommategroup.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class LeaderCannotLeaveException extends ApplicationException {

    public LeaderCannotLeaveException() {
        super(RoommateGroupErrorCode.LEADER_CANNOT_LEAVE);
    }
}
