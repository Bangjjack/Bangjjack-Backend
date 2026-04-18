package com.project.bangjjack.domain.chat.domain.service;

import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoomParticipant;
import com.project.bangjjack.domain.chat.domain.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomCreateService {

    private final ChatRoomRepository chatRoomRepository;

    // REQUIRES_NEW isolates this TX so DataIntegrityViolationException doesn't poison the caller's TX
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ChatRoom createDirectRoom(Long creatorId, Long targetId, String directRoomKey) {
        ChatRoom chatRoom = ChatRoom.createDirect(creatorId, directRoomKey);
        chatRoom.addParticipant(ChatRoomParticipant.create(chatRoom, creatorId));
        chatRoom.addParticipant(ChatRoomParticipant.create(chatRoom, targetId));
        return chatRoomRepository.save(chatRoom);
    }
}
