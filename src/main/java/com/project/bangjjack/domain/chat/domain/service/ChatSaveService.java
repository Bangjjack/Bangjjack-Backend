package com.project.bangjjack.domain.chat.domain.service;

import com.project.bangjjack.domain.chat.domain.entity.Chat;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.repository.ChatRepository;
import com.project.bangjjack.domain.chat.domain.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ChatSaveService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Async("chatTaskExecutor")
    @Transactional
    public CompletableFuture<Chat> saveAsync(Long senderId, Long roomId, String content) {
        ChatRoom chatRoom = chatRoomRepository.getReferenceById(roomId);
        Chat chat = Chat.create(senderId, chatRoom, content);
        return CompletableFuture.completedFuture(chatRepository.save(chat));
    }
}
