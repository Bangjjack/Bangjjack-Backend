package com.project.bangjjack.domain.chat.domain.repository;

import com.project.bangjjack.domain.chat.domain.entity.ChatRoomCategory;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoomParticipant;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ChatRoomParticipantQueryRepository {

    List<ChatRoomParticipant> findMyDirectParticipantsWithCursor(Long userId, Long cursorRoomId, LocalDateTime cursorLastMessageAt, int size, ChatRoomCategory category);

    Map<Long, Long> findPartnerIdsByDirectRoomIds(Collection<Long> roomIds, Long myUserId);
}
