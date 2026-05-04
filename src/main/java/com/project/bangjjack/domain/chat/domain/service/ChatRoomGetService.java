package com.project.bangjjack.domain.chat.domain.service;

import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoomParticipant;
import com.project.bangjjack.domain.chat.domain.repository.ChatRoomParticipantRepository;
import com.project.bangjjack.domain.chat.domain.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomGetService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;

    public Optional<ChatRoom> findByDirectRoomKey(String directRoomKey) {
        return chatRoomRepository.findByDirectRoomKey(directRoomKey);
    }

    public List<ChatRoomParticipant> findParticipantsByRoomId(Long roomId) {
        return chatRoomParticipantRepository.findByChatRoomIdAndDeletedFalse(roomId);
    }
}
