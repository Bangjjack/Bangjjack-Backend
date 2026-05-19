package com.project.bangjjack.domain.bookmark.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class AlreadyBookmarkedException extends ApplicationException {

    public AlreadyBookmarkedException() {
        super(BookmarkErrorCode.ALREADY_BOOKMARKED);
    }
}
