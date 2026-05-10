package com.project.bangjjack.domain.chat.application.usecase;

import com.project.bangjjack.domain.chat.application.dto.request.SendChatMessageRequest;
import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageAckResponse;
import com.project.bangjjack.domain.chat.application.event.ChatMessageSavedEvent;
import com.project.bangjjack.domain.chat.application.exception.ChatForbiddenException;
import com.project.bangjjack.domain.chat.application.exception.ChatRoomClosedException;
import com.project.bangjjack.domain.chat.domain.entity.Chat;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.service.ChatCreateService;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomGetService;
import com.project.bangjjack.domain.chat.infrastructure.redis.ChatIdempotencyService;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SendChatMessageUseCase {

    private final ChatRoomGetService chatRoomGetService;
    private final ChatCreateService chatCreateService;
    private final UserGetService userGetService;
    private final ApplicationEventPublisher eventPublisher;
    private final ChatIdempotencyService chatIdempotencyService;

    @Transactional
    public ChatMessageAckResponse execute(Long roomId, Long currentUserId, SendChatMessageRequest request) {
        Optional<ChatMessageAckResponse> duplicate = chatIdempotencyService.findDuplicate(currentUserId, request.clientTempId());
        if (duplicate.isPresent()) {
            return duplicate.get();
        }

        ChatRoom chatRoom = chatRoomGetService.getById(roomId);

        if (!chatRoomGetService.isParticipant(roomId, currentUserId)) {
            throw new ChatForbiddenException();
        }
        if (chatRoom.isClosed()) {
            throw new ChatRoomClosedException();
        }

        User sender = userGetService.getById(currentUserId);
        Chat chat = Chat.create(currentUserId, chatRoom, request.content());
        Chat saved = chatCreateService.save(chat);

        eventPublisher.publishEvent(new ChatMessageSavedEvent(
                saved.getId(),
                roomId,
                currentUserId,
                sender.getUsername(),
                sender.getProfileImage(),
                saved.getContent(),
                saved.getType(),
                saved.getCreatedAt(),
                request.clientTempId()
        ));

        return new ChatMessageAckResponse(saved.getId(), request.clientTempId(), saved.getCreatedAt());
    }
}
