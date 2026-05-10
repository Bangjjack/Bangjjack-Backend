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
    POST_NOT_EDITABLE(40707, HttpStatus.CONFLICT, "마감된 모집글은 수정할 수 없습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
