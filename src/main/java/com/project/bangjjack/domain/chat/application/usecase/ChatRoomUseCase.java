package com.project.bangjjack.domain.chat.application.usecase;

import com.project.bangjjack.domain.chat.application.dto.request.CreateChatRoomRequest;
import com.project.bangjjack.domain.chat.application.dto.response.ChatRoomResponse;
import com.project.bangjjack.domain.chat.application.exception.CannotChatWithSelfException;
import com.project.bangjjack.domain.chat.application.exception.ChatRoomNotFoundException;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomCreateService;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomGetService;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomUseCase {

    private final ChatRoomCreateService chatRoomCreateService;
    private final ChatRoomGetService chatRoomGetService;
    private final UserGetService userGetService;

    @Transactional
    public ChatRoomResponse createDirectRoom(Long currentUserId, CreateChatRoomRequest request) {
        if (currentUserId.equals(request.targetUserId())) {
            throw new CannotChatWithSelfException();
        }
        userGetService.getById(request.targetUserId());

        String directRoomKey = ChatRoom.generateDirectKey(currentUserId, request.targetUserId());

        Optional<ChatRoom> existing = chatRoomGetService.findByDirectRoomKey(directRoomKey);
        if (existing.isPresent()) {
            return ChatRoomResponse.from(existing.get(), false);
        }

        try {
            ChatRoom chatRoom = chatRoomCreateService.createDirectRoom(currentUserId, request.targetUserId(), directRoomKey);
            return ChatRoomResponse.from(chatRoom, true);
        } catch (DataIntegrityViolationException e) {
            // 동시 요청으로 먼저 생성된 방이 있는 경우 재조회
            ChatRoom chatRoom = chatRoomGetService.findByDirectRoomKey(directRoomKey)
                    .orElseThrow(ChatRoomNotFoundException::new);
            return ChatRoomResponse.from(chatRoom, false);
        }
    }
}
