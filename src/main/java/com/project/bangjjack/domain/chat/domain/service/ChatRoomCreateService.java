package com.project.bangjjack.domain.chat.domain.service;

import com.project.bangjjack.domain.chat.application.event.ChatRoomCreatedEvent;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoomParticipant;
import com.project.bangjjack.domain.chat.domain.repository.ChatRoomParticipantRepository;
import com.project.bangjjack.domain.chat.domain.repository.ChatRoomRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomCreateService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final ApplicationEventPublisher eventPublisher;

    public String createDirectKey(Long userId1, Long userId2) {
        return ChatRoom.generateDirectKey(userId1, userId2);
    }

    @Transactional
    public ChatRoom createDirectRoom(Long creatorId, Long targetId, String directRoomKey) {
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.createDirect(creatorId, directRoomKey));
        List<ChatRoomParticipant> participants = List.of(
                ChatRoomParticipant.create(chatRoom, creatorId),
                ChatRoomParticipant.create(chatRoom, targetId)
        );
        chatRoomParticipantRepository.saveAll(participants);
        eventPublisher.publishEvent(new ChatRoomCreatedEvent(chatRoom.getId(), List.of(creatorId, targetId)));
        return chatRoom;
    }
}
