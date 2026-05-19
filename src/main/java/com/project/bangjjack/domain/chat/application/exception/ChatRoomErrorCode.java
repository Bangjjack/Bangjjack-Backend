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
    NOT_CHAT_PARTICIPANT(40604, HttpStatus.FORBIDDEN, "채팅방 참여자가 아닙니다."),
    NOT_SUBSCRIBED(40605, HttpStatus.BAD_REQUEST, "먼저 해당 채팅방을 구독해야 합니다."),
    INVALID_MESSAGE_CONTENT(40606, HttpStatus.BAD_REQUEST, "유효하지 않은 메시지 내용입니다."),
    INVALID_WS_MESSAGE_FORMAT(40607, HttpStatus.BAD_REQUEST, "메시지 형식이 올바르지 않습니다."),
    MISSING_MESSAGE_TYPE(40608, HttpStatus.BAD_REQUEST, "메시지 타입이 없습니다."),
    WS_INTERNAL_ERROR(50601, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
