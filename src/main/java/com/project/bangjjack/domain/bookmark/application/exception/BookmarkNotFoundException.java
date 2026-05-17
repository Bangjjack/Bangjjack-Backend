package com.project.bangjjack.domain.bookmark.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class BookmarkNotFoundException extends ApplicationException {

    public BookmarkNotFoundException() {
        super(BookmarkErrorCode.BOOKMARK_NOT_FOUND);
    }
}
