package com.project.bangjjack.domain.bookmark.application.exception;

import com.project.bangjjack.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BookmarkErrorCode implements ErrorCodeInterface {

    ALREADY_BOOKMARKED(40801, HttpStatus.CONFLICT, "이미 북마크한 게시글입니다."),
    BOOKMARK_NOT_FOUND(40802, HttpStatus.NOT_FOUND, "북마크를 찾을 수 없습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
