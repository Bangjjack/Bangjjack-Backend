package com.project.bangjjack.domain.chat.domain.service;

import com.project.bangjjack.domain.chat.domain.entity.Chat;
import com.project.bangjjack.domain.chat.domain.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatCreateService {

    private final ChatRepository chatRepository;

    public Chat save(Chat chat) {
        return chatRepository.save(chat);
    }
}
