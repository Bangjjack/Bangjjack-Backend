package com.project.bangjjack.domain.application.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class RecruitLimitReachedException extends ApplicationException {

    public RecruitLimitReachedException() {
        super(ApplicationErrorCode.RECRUIT_LIMIT_REACHED);
    }
}
