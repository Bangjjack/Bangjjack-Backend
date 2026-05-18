package com.project.bangjjack.domain.post.presentation.response;

import com.project.bangjjack.global.common.response.ResponseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostResponseCode implements ResponseCodeInterface {

    POST_CREATED(201, HttpStatus.CREATED, "룸메이트 모집글이 작성되었습니다."),
    POST_UPDATED(200, HttpStatus.OK, "룸메이트 모집글이 수정되었습니다."),
    POST_DELETED(200, HttpStatus.OK, "룸메이트 모집글이 삭제되었습니다."),
    POST_DETAIL_FOUND(200, HttpStatus.OK, "룸메이트 모집글 상세 조회에 성공했습니다."),
    MATCH_RATE_ANALYZED(200, HttpStatus.OK, "AI 매칭률 분석에 성공했습니다."),
    POST_LIST_FOUND(200, HttpStatus.OK, "룸메이트 모집글 목록 조회에 성공했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
