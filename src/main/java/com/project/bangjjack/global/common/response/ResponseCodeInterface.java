package com.project.bangjjack.global.common.response;

import org.springframework.http.HttpStatus;

public interface ResponseCodeInterface {
    int getCode();
    HttpStatus getStatus();
    String getMessage();
}
