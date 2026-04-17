package com.project.bangjjack.domain.user.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class InvalidBirthYearException extends ApplicationException {

    public InvalidBirthYearException() {
        super(UserErrorCode.INVALID_BIRTH_YEAR);
    }
}
