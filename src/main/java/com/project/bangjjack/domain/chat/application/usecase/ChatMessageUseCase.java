package com.project.bangjjack.domain.chat.application.usecase;

import com.project.bangjjack.domain.chat.application.dto.request.SendChatMessageRequest;
import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageBroadcast;
import com.project.bangjjack.domain.chat.application.exception.ChatRoomClosedException;
import com.project.bangjjack.domain.chat.domain.entity.Chat;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.entity.RoomStatus;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomGetService;
import com.project.bangjjack.domain.chat.domain.service.ChatSaveService;
import com.project.bangjjack.domain.chat.infrastructure.ChatMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageUseCase {

    private final ChatRoomGetService chatRoomGetService;
    private final ChatSaveService chatSaveService;
    private final ChatMessagePublisher chatMessagePublisher;

    @Transactional
    public void sendMessage(Long senderId, Long roomId, SendChatMessageRequest request) {
        log.debug("[채팅 송신] 시작 - senderId={}, roomId={}, content='{}'", senderId, roomId, request.content());

        ChatRoom chatRoom = chatRoomGetService.getById(roomId);
        log.debug("[채팅 송신] 채팅방 조회 완료 - roomId={}, status={}", roomId, chatRoom.getStatus());

        if (chatRoom.getStatus() == RoomStatus.CLOSED) {
            log.warn("[채팅 송신] 종료된 채팅방 전송 시도 - senderId={}, roomId={}", senderId, roomId);
            throw new ChatRoomClosedException();
        }

        chatRoomGetService.validateParticipant(roomId, senderId);
        log.debug("[채팅 송신] 참여자 검증 완료 - senderId={}, roomId={}", senderId, roomId);

        CompletableFuture<Chat> saveFuture = chatSaveService.saveAsync(senderId, roomId, request.content());
        Chat savedChat = getSavedChat(saveFuture);
        log.debug("[채팅 송신] DB 저장 완료 - messageId={}", savedChat.getId());

        ChatMessageBroadcast broadcast = ChatMessageBroadcast.from(savedChat, roomId);
        chatMessagePublisher.publish(roomId, broadcast);
        log.debug("[채팅 송신] 브로드캐스트 발행 완료 - messageId={}, roomId={}", savedChat.getId(), roomId);
    }

    private Chat getSavedChat(CompletableFuture<Chat> saveFuture) {
        try {
            return saveFuture.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("[채팅 송신] 메시지 저장 실패 - 원인={}", e.getMessage(), e);
            throw new RuntimeException("메시지 저장에 실패했습니다.", e);
        }
    }
}
