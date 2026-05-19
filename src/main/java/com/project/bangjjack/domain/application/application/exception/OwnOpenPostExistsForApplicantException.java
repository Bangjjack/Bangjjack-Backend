package com.project.bangjjack.domain.application.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class OwnOpenPostExistsForApplicantException extends ApplicationException {

    public OwnOpenPostExistsForApplicantException() {
        super(ApplicationErrorCode.OWN_OPEN_POST_EXISTS_FOR_APPLICANT);
    }
}
