package com.project.bangjjack.domain.chat.presentation.response;

import com.project.bangjjack.global.common.response.ResponseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatRoomResponseCode implements ResponseCodeInterface {

    CHAT_ROOM_CREATED(201, HttpStatus.CREATED, "채팅방이 생성되었습니다."),
    CHAT_ROOM_FOUND(200, HttpStatus.OK, "기존 채팅방을 반환합니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
