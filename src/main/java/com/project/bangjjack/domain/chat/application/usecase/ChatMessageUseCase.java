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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageUseCase {

    private final ChatRoomGetService chatRoomGetService;
    private final ChatSaveService chatSaveService;
    private final ChatMessagePublisher chatMessagePublisher;

    @Transactional
    public void sendMessage(Long senderId, Long roomId, SendChatMessageRequest request) {
        ChatRoom chatRoom = chatRoomGetService.getById(roomId);

        if (chatRoom.getStatus() == RoomStatus.CLOSED) {
            throw new ChatRoomClosedException();
        }

        chatRoomGetService.validateParticipant(roomId, senderId);

        CompletableFuture<Chat> saveFuture = chatSaveService.saveAsync(senderId, roomId, request.content());
        Chat savedChat = getSavedChat(saveFuture);

        ChatMessageBroadcast broadcast = ChatMessageBroadcast.from(savedChat, roomId);
        chatMessagePublisher.publish(roomId, broadcast);
    }

    private Chat getSavedChat(CompletableFuture<Chat> saveFuture) {
        try {
            return saveFuture.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("메시지 저장에 실패했습니다.", e);
        }
    }
}
