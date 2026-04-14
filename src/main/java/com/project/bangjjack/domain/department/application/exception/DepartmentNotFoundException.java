package com.project.bangjjack.domain.department.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class DepartmentNotFoundException extends ApplicationException {

    public DepartmentNotFoundException() {
        super(DepartmentErrorCode.DEPARTMENT_NOT_FOUND);
    }
}
