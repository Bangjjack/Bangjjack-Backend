package com.project.bangjjack.domain.department.application.exception;

import com.project.bangjjack.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DepartmentErrorCode implements ErrorCodeInterface {

    DEPARTMENT_NOT_FOUND(40104, HttpStatus.NOT_FOUND, "존재하지 않는 학과입니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
