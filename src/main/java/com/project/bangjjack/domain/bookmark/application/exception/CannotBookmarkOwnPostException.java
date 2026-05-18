package com.project.bangjjack.domain.bookmark.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class CannotBookmarkOwnPostException extends ApplicationException {

    public CannotBookmarkOwnPostException() {
        super(BookmarkErrorCode.CANNOT_BOOKMARK_OWN_POST);
    }
}
