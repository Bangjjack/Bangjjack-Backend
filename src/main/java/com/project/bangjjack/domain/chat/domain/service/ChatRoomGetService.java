package com.project.bangjjack.domain.chat.domain.service;

import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomGetService {

    private final ChatRoomRepository chatRoomRepository;

    public Optional<ChatRoom> findByDirectRoomKey(String directRoomKey) {
        return chatRoomRepository.findByDirectRoomKeyWithParticipants(directRoomKey);
    }
}
