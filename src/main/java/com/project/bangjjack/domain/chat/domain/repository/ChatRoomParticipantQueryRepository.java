package com.project.bangjjack.domain.chat.domain.repository;

import com.project.bangjjack.domain.chat.domain.entity.ChatRoomCategory;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoomParticipant;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ChatRoomParticipantQueryRepository {

    List<Long> findRoomIdsByUserIdWithCursor(Long userId, Long cursorRoomId, LocalDateTime cursorLastMessageAt, int size, ChatRoomCategory category);

    List<ChatRoomParticipant> findAllWithRoomByRoomIds(Collection<Long> roomIds);
}
