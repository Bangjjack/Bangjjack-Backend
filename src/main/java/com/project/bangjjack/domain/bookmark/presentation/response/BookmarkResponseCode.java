package com.project.bangjjack.domain.bookmark.presentation.response;

import com.project.bangjjack.global.common.response.ResponseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BookmarkResponseCode implements ResponseCodeInterface {

    BOOKMARK_CREATED(201, HttpStatus.CREATED, "북마크가 등록되었습니다."),
    BOOKMARK_DELETED(200, HttpStatus.OK, "북마크가 해제되었습니다."),
    BOOKMARKED_POST_LIST_FOUND(200, HttpStatus.OK, "북마크한 게시글 목록 조회에 성공했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
