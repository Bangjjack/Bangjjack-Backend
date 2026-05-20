package com.project.bangjjack.domain.chat.domain.repository;

import com.project.bangjjack.domain.chat.domain.entity.ChatRoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long> {

    List<ChatRoomParticipant> findByChatRoomIdAndDeletedFalse(Long chatRoomId);

    boolean existsByChatRoomIdAndUserIdAndDeletedFalse(Long chatRoomId, Long userId);

    @Query("SELECT p.chatRoom.id FROM ChatRoomParticipant p WHERE p.userId = :userId AND p.deleted = false AND p.chatRoom.deleted = false")
    List<Long> findRoomIdsByUserIdAndDeletedFalse(@Param("userId") Long userId);
}
