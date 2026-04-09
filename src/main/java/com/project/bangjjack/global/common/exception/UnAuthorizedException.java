package com.project.bangjjack.global.common.exception;

public class UnAuthorizedException extends ApplicationException {
    public UnAuthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}
