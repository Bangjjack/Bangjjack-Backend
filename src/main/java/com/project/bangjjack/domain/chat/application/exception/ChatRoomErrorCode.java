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
    CHAT_ROOM_CLOSED(40603, HttpStatus.BAD_REQUEST, "종료된 채팅방에는 메시지를 전송할 수 없습니다."),
    NOT_CHAT_PARTICIPANT(40604, HttpStatus.FORBIDDEN, "채팅방 참여자가 아닙니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
