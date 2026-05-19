package com.project.bangjjack.domain.post.application.exception;

import com.project.bangjjack.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostErrorCode implements ErrorCodeInterface {

    ALREADY_OPEN_POST_EXISTS(40701, HttpStatus.CONFLICT, "이미 모집 중인 룸메이트 모집글이 존재합니다."),
    POST_WRITE_PRECONDITION_NOT_MET(40702, HttpStatus.BAD_REQUEST, "모집글 작성 전 온보딩, 체크리스트, 선호도 등록을 완료해주세요."),
    INVALID_RECRUIT_MEMBER_COUNT(40703, HttpStatus.BAD_REQUEST, "모집 인원이 방 유형과 일치하지 않습니다."),
    POST_NOT_FOUND(40704, HttpStatus.NOT_FOUND, "모집글을 찾을 수 없습니다."),
    POST_DELETE_FORBIDDEN(40705, HttpStatus.FORBIDDEN, "모집글을 삭제할 권한이 없습니다."),
    POST_UPDATE_FORBIDDEN(40706, HttpStatus.FORBIDDEN, "모집글을 수정할 권한이 없습니다."),
    POST_NOT_EDITABLE(40707, HttpStatus.UNPROCESSABLE_ENTITY, "마감된 모집글은 수정할 수 없습니다."),
    SHARED_LIFESTYLE_NOT_FOUND(40708, HttpStatus.NOT_FOUND, "모집글의 공동생활 정보를 찾을 수 없습니다."),
    SELF_MATCH_NOT_ALLOWED(40709, HttpStatus.CONFLICT, "본인이 작성한 모집글에는 매칭 분석을 요청할 수 없습니다."),
    CHECKLIST_NOT_REGISTERED(40710, HttpStatus.CONFLICT, "매칭 분석을 위해 양측 모두 생활습관 체크리스트 등록이 필요합니다."),
    PREFERENCE_NOT_REGISTERED(40711, HttpStatus.CONFLICT, "매칭 분석을 위해 양측 모두 룸메이트 선호도 등록이 필요합니다."),
    AI_SERVICE_UNAVAILABLE(40712, HttpStatus.BAD_GATEWAY, "AI 매칭 분석 서비스에 일시적으로 접근할 수 없습니다. 잠시 후 다시 시도해주세요.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
