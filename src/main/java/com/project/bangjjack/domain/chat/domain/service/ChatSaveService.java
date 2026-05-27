package com.project.bangjjack.domain.chat.domain.service;

import com.project.bangjjack.domain.chat.domain.entity.Chat;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.entity.MessageType;
import com.project.bangjjack.domain.chat.domain.repository.ChatRepository;
import com.project.bangjjack.domain.chat.domain.repository.ChatRoomParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatSaveService {

    private final ChatRepository chatRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;

    @Transactional
    public Chat save(Long senderId, ChatRoom chatRoom, String content, MessageType messageType) {
        return save(senderId, chatRoom, content, messageType, null);
    }

    @Transactional
    public Chat save(Long senderId, ChatRoom chatRoom, String content, MessageType messageType, Long applicationId) {
        Chat saved = chatRepository.save(Chat.create(senderId, chatRoom, content, messageType, applicationId));
        chatRoom.updateLastMessageAt(saved.getCreatedAt());
        chatRoomParticipantRepository.incrementUnreadCountExcludingSender(chatRoom.getId(), senderId);
        return saved;
    }
}
