package com.project.bangjjack.domain.post.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class InvalidRecruitMemberCountException extends ApplicationException {

    public InvalidRecruitMemberCountException() {
        super(PostErrorCode.INVALID_RECRUIT_MEMBER_COUNT);
    }
}
