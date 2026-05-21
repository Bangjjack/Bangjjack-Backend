package com.project.bangjjack.domain.chat.domain.service;

import com.project.bangjjack.domain.chat.domain.entity.Chat;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.entity.MessageType;
import com.project.bangjjack.domain.chat.domain.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatSaveService {

    private final ChatRepository chatRepository;

    @Transactional
    public Chat save(Long senderId, ChatRoom chatRoom, String content, MessageType messageType) {
        Chat chat = Chat.create(senderId, chatRoom, content, messageType);
        return chatRepository.save(chat);
    }
}
