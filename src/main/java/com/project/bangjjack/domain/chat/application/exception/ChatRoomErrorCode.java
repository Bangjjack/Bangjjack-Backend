package com.project.bangjjack.domain.chat.application.exception;

import com.project.bangjjack.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatRoomErrorCode implements ErrorCodeInterface {

    CANNOT_CHAT_WITH_SELF(40601, HttpStatus.BAD_REQUEST, "자기 자신과 채팅방을 생성할 수 없습니다."),
    CHAT_ROOM_NOT_FOUND(40602, HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_FORBIDDEN(40603, HttpStatus.FORBIDDEN, "해당 채팅방에 접근할 권한이 없습니다."),
    CHAT_ROOM_CLOSED(40604, HttpStatus.CONFLICT, "종료된 채팅방에는 메시지를 전송할 수 없습니다."),
    INVALID_CHAT_MESSAGE(40605, HttpStatus.BAD_REQUEST, "유효하지 않은 채팅 메시지입니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
