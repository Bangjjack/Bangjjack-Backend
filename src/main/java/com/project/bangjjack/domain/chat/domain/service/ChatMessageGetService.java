package com.project.bangjjack.domain.chat.domain.service;

import com.project.bangjjack.domain.chat.domain.entity.Chat;
import com.project.bangjjack.domain.chat.domain.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageGetService {

    private final ChatRepository chatRepository;

    public List<Chat> getMessages(Long roomId, Long cursor, int size) {
        long cursorId = cursor != null ? cursor : Long.MAX_VALUE;
        return chatRepository.findByCursorPage(roomId, cursorId, PageRequest.of(0, size + 1));
    }

    public Map<Long, Chat> findLastMessagesByRoomIds(Collection<Long> roomIds) {
        return chatRepository.findLastMessagesByRoomIds(roomIds).stream()
                .collect(Collectors.toMap(c -> c.getChatRoom().getId(), c -> c));
    }
}
