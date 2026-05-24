package com.project.bangjjack.domain.chat.application.usecase;

import com.project.bangjjack.domain.chat.application.dto.request.SendChatMessageRequest;
import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageBroadcast;
import com.project.bangjjack.domain.chat.application.dto.response.ChatMessagePageResponse;
import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageResponse;
import com.project.bangjjack.domain.chat.application.event.ChatMessageSentEvent;
import com.project.bangjjack.domain.chat.application.exception.ChatRoomClosedException;
import com.project.bangjjack.domain.chat.application.exception.InvalidMessageContentException;
import com.project.bangjjack.domain.chat.domain.entity.Chat;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoomParticipant;
import com.project.bangjjack.domain.chat.domain.entity.MessageType;
import com.project.bangjjack.domain.chat.domain.entity.RoomStatus;
import com.project.bangjjack.domain.chat.domain.service.ChatMessageGetService;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomGetService;
import com.project.bangjjack.domain.chat.domain.service.ChatSaveService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageUseCase {

    private final ChatRoomGetService chatRoomGetService;
    private final ChatSaveService chatSaveService;
    private final ChatMessageGetService chatMessageGetService;
    private final ApplicationEventPublisher eventPublisher;
    private final Validator validator;

    @Transactional
    public void sendMessage(Long senderId, Long roomId, SendChatMessageRequest request) {
        if (request == null) {
            throw new InvalidMessageContentException();
        }
        // WebSocket은 @Valid 자동 적용 대상이 아니므로 수동 검증
        Set<ConstraintViolation<SendChatMessageRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new InvalidMessageContentException();
        }
        // TODO: 운영 환경 전환 시 content 제거 또는 contentLength로 변경 (PII)
        log.debug("[채팅 송신] 시작 - senderId={}, roomId={}, content='{}'", senderId, roomId, request.content());

        ChatRoom chatRoom = chatRoomGetService.getById(roomId);

        if (chatRoom.getStatus() == RoomStatus.CLOSED) {
            log.warn("[채팅 송신] 종료된 채팅방 전송 시도 - senderId={}, roomId={}", senderId, roomId);
            throw new ChatRoomClosedException();
        }

        chatRoomGetService.validateActiveParticipant(roomId, senderId);

        chatRoomGetService.findLeftParticipantsByRoomId(roomId, senderId)
                .forEach(ChatRoomParticipant::rejoin);

        Chat savedChat = chatSaveService.save(senderId, chatRoom, request.content(), MessageType.USER);
        log.debug("[채팅 송신] DB 저장 완료 - messageId={}", savedChat.getId());

        ChatMessageBroadcast broadcast = ChatMessageBroadcast.from(savedChat, roomId);
        eventPublisher.publishEvent(new ChatMessageSentEvent(roomId, broadcast));
        log.debug("[채팅 송신] 브로드캐스트 이벤트 발행 완료 - messageId={}, roomId={}", savedChat.getId(), roomId);
    }

    @Transactional
    public ChatMessagePageResponse getMessages(Long currentUserId, Long roomId, Long cursor, int size) {
        ChatRoomParticipant participant = chatRoomGetService.getActiveParticipant(roomId, currentUserId);

        List<Chat> fetched = chatMessageGetService.getMessages(roomId, cursor, size);

        boolean hasNext = fetched.size() > size;
        List<Chat> content = hasNext ? fetched.subList(0, size) : fetched;

        if (!content.isEmpty()) {
            participant.markAsRead(content.get(0).getId());
        }
        Long nextCursor = hasNext ? content.get(content.size() - 1).getId() : null;

        List<ChatMessageResponse> messages = content.stream()
                .map(ChatMessageResponse::from)
                .toList();

        return ChatMessagePageResponse.of(messages, nextCursor, hasNext);
    }
}
