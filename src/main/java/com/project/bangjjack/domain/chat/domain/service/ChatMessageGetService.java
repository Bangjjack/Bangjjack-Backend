package com.project.bangjjack.domain.chat.domain.service;

import com.project.bangjjack.domain.chat.application.dto.response.ChatMessagePageResponse;
import com.project.bangjjack.domain.chat.application.dto.response.ChatMessageResponse;
import com.project.bangjjack.domain.chat.domain.entity.Chat;
import com.project.bangjjack.domain.chat.domain.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageGetService {

    private final ChatRepository chatRepository;

    public ChatMessagePageResponse getMessages(Long roomId, Long cursor, int size) {
        long cursorId = cursor != null ? cursor : Long.MAX_VALUE;
        List<Chat> fetched = chatRepository.findByCursorPage(roomId, cursorId, PageRequest.of(0, size + 1));

        boolean hasNext = fetched.size() > size;
        List<Chat> content = hasNext ? fetched.subList(0, size) : fetched;
        Long nextCursor = hasNext ? content.get(content.size() - 1).getId() : null;

        List<ChatMessageResponse> messages = content.stream()
                .map(ChatMessageResponse::from)
                .toList();

        return new ChatMessagePageResponse(messages, nextCursor, hasNext);
    }
}
