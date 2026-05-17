package com.project.bangjjack.domain.chat.application.usecase;

import com.project.bangjjack.domain.chat.application.dto.request.SendChatMessageRequest;
import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageBroadcast;
import com.project.bangjjack.domain.chat.application.event.ChatMessageSentEvent;
import com.project.bangjjack.domain.chat.application.exception.ChatRoomClosedException;
import com.project.bangjjack.domain.chat.domain.entity.Chat;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.entity.RoomStatus;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomGetService;
import com.project.bangjjack.domain.chat.domain.service.ChatSaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageUseCase {

    private final ChatRoomGetService chatRoomGetService;
    private final ChatSaveService chatSaveService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void sendMessage(Long senderId, Long roomId, SendChatMessageRequest request) {
        // TODO: 운영 환경 전환 시 content 제거 또는 contentLength로 변경 (PII)
        log.debug("[채팅 송신] 시작 - senderId={}, roomId={}, content='{}'", senderId, roomId, request.content());

        ChatRoom chatRoom = chatRoomGetService.getByIdAndValidateParticipant(roomId, senderId);

        if (chatRoom.getStatus() == RoomStatus.CLOSED) {
            log.warn("[채팅 송신] 종료된 채팅방 전송 시도 - senderId={}, roomId={}", senderId, roomId);
            throw new ChatRoomClosedException();
        }

        Chat savedChat = chatSaveService.save(senderId, chatRoom, request.content());
        log.debug("[채팅 송신] DB 저장 완료 - messageId={}", savedChat.getId());

        ChatMessageBroadcast broadcast = ChatMessageBroadcast.from(savedChat, roomId);
        eventPublisher.publishEvent(new ChatMessageSentEvent(roomId, broadcast));
        log.debug("[채팅 송신] 브로드캐스트 이벤트 발행 완료 - messageId={}, roomId={}", savedChat.getId(), roomId);
    }

}
