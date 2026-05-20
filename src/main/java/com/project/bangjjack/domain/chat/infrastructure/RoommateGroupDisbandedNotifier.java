package com.project.bangjjack.domain.chat.infrastructure;

import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageBroadcast;
import com.project.bangjjack.domain.chat.application.event.ChatMessageSentEvent;
import com.project.bangjjack.domain.chat.domain.entity.Chat;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import com.project.bangjjack.domain.chat.domain.entity.MessageType;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomCreateService;
import com.project.bangjjack.domain.chat.domain.service.ChatRoomGetService;
import com.project.bangjjack.domain.chat.domain.service.ChatSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RoommateGroupDisbandedNotifier {

    private final ChatRoomGetService chatRoomGetService;
    private final ChatRoomCreateService chatRoomCreateService;
    private final ChatSaveService chatSaveService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void sendDirectSystemMessage(Long senderId, Long receiverId, String content) {
        String directRoomKey = ChatRoom.generateDirectKey(senderId, receiverId);
        ChatRoom room = chatRoomGetService.findByDirectRoomKey(directRoomKey)
                .orElseGet(() -> chatRoomCreateService.createDirectRoom(senderId, receiverId, directRoomKey));
        Chat saved = chatSaveService.save(senderId, room, content, MessageType.GROUP_DISBANDED);
        ChatMessageBroadcast broadcast = ChatMessageBroadcast.from(saved, room.getId());
        eventPublisher.publishEvent(new ChatMessageSentEvent(room.getId(), broadcast));
    }
}
