package com.project.bangjjack.domain.chat.presentation;

import com.project.bangjjack.domain.chat.application.dto.request.CreateChatRoomRequest;
import com.project.bangjjack.domain.chat.application.dto.response.ChatRoomResponse;
import com.project.bangjjack.domain.chat.application.usecase.ChatRoomUseCase;
import com.project.bangjjack.domain.chat.presentation.response.ChatRoomResponseCode;
import com.project.bangjjack.global.annotation.CurrentMemberId;
import com.project.bangjjack.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ChatRoom", description = "채팅방 관련 API")
@RestController
@RequestMapping("/api/v1/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomUseCase chatRoomUseCase;

    @Operation(summary = "1:1 채팅방 생성", description = "특정 유저와 1:1 채팅방을 생성합니다. 이미 존재하는 경우 기존 채팅방을 반환합니다.")
    @PostMapping
    public CommonResponse<ChatRoomResponse> createDirectRoom(
            @CurrentMemberId Long currentMemberId,
            @RequestBody @Valid CreateChatRoomRequest request) {
        ChatRoomResponse response = chatRoomUseCase.createDirectRoom(currentMemberId, request);
        ChatRoomResponseCode responseCode = response.isNewRoom()
                ? ChatRoomResponseCode.CHAT_ROOM_CREATED
                : ChatRoomResponseCode.CHAT_ROOM_FOUND;
        return CommonResponse.success(responseCode, response);
    }
}
