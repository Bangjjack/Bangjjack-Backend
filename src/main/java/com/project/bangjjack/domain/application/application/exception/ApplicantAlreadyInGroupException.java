package com.project.bangjjack.domain.application.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class ApplicantAlreadyInGroupException extends ApplicationException {

    public ApplicantAlreadyInGroupException() {
        super(ApplicationErrorCode.APPLICANT_ALREADY_IN_GROUP);
    }
}
