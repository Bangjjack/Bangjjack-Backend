package com.project.bangjjack.domain.roommategroup.application.exception;

import com.project.bangjjack.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RoommateGroupErrorCode implements ErrorCodeInterface {

    ROOMMATE_GROUP_NOT_FOUND(40901, HttpStatus.INTERNAL_SERVER_ERROR, "모집글에 연결된 룸메이트 그룹을 찾을 수 없습니다."),
    ROOMMATE_GROUP_MEMBERSHIP_NOT_FOUND(40902, HttpStatus.NOT_FOUND, "해당 룸메이트 그룹의 멤버가 아닙니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
