package com.project.bangjjack.domain.application.application.exception;

import com.project.bangjjack.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApplicationErrorCode implements ErrorCodeInterface {

    APPLICATION_PRECONDITION_NOT_MET(40801, HttpStatus.BAD_REQUEST, "신청 전 온보딩, 체크리스트, 선호도 등록을 완료해주세요."),
    CANNOT_APPLY_TO_OWN_POST(40802, HttpStatus.BAD_REQUEST, "본인이 작성한 모집글에는 신청할 수 없습니다."),
    ALREADY_APPLIED_PENDING(40804, HttpStatus.CONFLICT, "이미 처리되지 않은 신청이 존재합니다."),
    OWN_OPEN_POST_EXISTS_FOR_APPLICANT(40805, HttpStatus.CONFLICT, "본인의 모집글이 모집 중인 동안에는 다른 모집글에 신청할 수 없습니다."),
    APPLICATION_NOT_FOUND(40806, HttpStatus.NOT_FOUND, "신청을 찾을 수 없습니다."),
    APPLICATION_PROCESS_FORBIDDEN(40807, HttpStatus.FORBIDDEN, "신청을 처리할 권한이 없습니다."),
    INVALID_APPLICATION_STATUS(40808, HttpStatus.CONFLICT, "이미 처리된 신청은 다시 처리할 수 없습니다."),
    INVALID_TARGET_STATUS(40809, HttpStatus.BAD_REQUEST, "요청한 상태값이 유효하지 않습니다."),
    RECRUIT_LIMIT_REACHED(40810, HttpStatus.CONFLICT, "모집 정원이 가득 차 더 이상 수락할 수 없습니다."),
    APPLICANT_ALREADY_IN_GROUP(40811, HttpStatus.CONFLICT, "신청자가 이미 다른 룸메이트 그룹에 소속되어 있습니다."),
    NO_OPEN_POST_FOR_USER(40812, HttpStatus.NOT_FOUND, "해당 사용자는 현재 모집 중인 룸메이트 모집글이 없습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
