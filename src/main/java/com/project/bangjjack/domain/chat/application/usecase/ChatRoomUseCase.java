package com.project.bangjjack.domain.chat.application.usecase;

import com.project.bangjjack.domain.chat.application.dto.request.CreateChatRoomRequest;
import com.project.bangjjack.domain.chat.application.dto.response.ChatRoomResponse;
import com.project.bangjjack.domain.chat.application.event.ChatRoomCreatedEvent;
import com.project.bangjjack.domain.chat.application.exception.CannotChatWithSelfException;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoomParticipant;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomCreateService;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomGetService;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomUseCase {

    private final ChatRoomCreateService chatRoomCreateService;
    private final ChatRoomGetService chatRoomGetService;
    private final UserGetService userGetService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ChatRoomResponse createDirectRoom(Long currentUserId, CreateChatRoomRequest request) {
        if (currentUserId.equals(request.targetUserId())) {
            throw new CannotChatWithSelfException();
        }
        userGetService.getById(request.targetUserId());

        String directRoomKey = chatRoomCreateService.createDirectKey(currentUserId, request.targetUserId());

        Optional<ChatRoom> existing = chatRoomGetService.findByDirectRoomKey(directRoomKey);
        if (existing.isPresent()) {
            ChatRoom chatRoom = existing.get();
            List<ChatRoomParticipant> participants = chatRoomGetService.findParticipantsByRoomId(chatRoom.getId());
            return ChatRoomResponse.from(chatRoom, participants, false);
        }

        ChatRoom chatRoom = chatRoomCreateService.createDirectRoom(currentUserId, request.targetUserId(), directRoomKey);
        List<ChatRoomParticipant> participants = chatRoomGetService.findParticipantsByRoomId(chatRoom.getId());
        eventPublisher.publishEvent(new ChatRoomCreatedEvent(chatRoom.getId(), List.of(currentUserId, request.targetUserId())));
        return ChatRoomResponse.from(chatRoom, participants, true);
    }

    public List<Long> getMyRoomIds(Long userId) {
        return chatRoomGetService.findRoomIdsByUserId(userId);
    }

    public void validateParticipant(Long roomId, Long userId) {
        chatRoomGetService.validateParticipant(roomId, userId);
    }
}
