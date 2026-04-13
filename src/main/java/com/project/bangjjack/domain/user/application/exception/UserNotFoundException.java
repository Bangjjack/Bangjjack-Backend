package com.project.bangjjack.domain.user.application.exception;

import com.project.bangjjack.global.common.exception.ApplicationException;

public class UserNotFoundException extends ApplicationException {

    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }
}
