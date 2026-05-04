package com.project.bangjjack.domain.chat.application.exception;

import com.project.bangjjack.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatRoomErrorCode implements ErrorCodeInterface {

    CANNOT_CHAT_WITH_SELF(40601, HttpStatus.BAD_REQUEST, "자기 자신과 채팅방을 생성할 수 없습니다."),
    CHAT_ROOM_NOT_FOUND(40602, HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
