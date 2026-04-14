package com.project.bangjjack.domain.department.presentation.response;

import com.project.bangjjack.global.common.response.ResponseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DepartmentResponseCode implements ResponseCodeInterface {

    DEPARTMENTS_FOUND(200, HttpStatus.OK, "학과 목록 조회에 성공했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
