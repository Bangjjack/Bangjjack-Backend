package com.project.bangjjack.domain.department.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class InvalidCampusException extends ApplicationException {

    public InvalidCampusException() {
        super(DepartmentErrorCode.INVALID_CAMPUS);
    }
}
