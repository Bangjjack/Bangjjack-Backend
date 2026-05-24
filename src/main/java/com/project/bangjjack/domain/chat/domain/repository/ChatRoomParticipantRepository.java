package com.project.bangjjack.domain.chat.domain.repository;

import com.project.bangjjack.domain.chat.domain.entity.ChatRoomParticipant;
import com.project.bangjjack.domain.chat.domain.entity.ParticipantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipant, Long>, ChatRoomParticipantQueryRepository {

    List<ChatRoomParticipant> findByChatRoomIdAndStatusAndDeletedFalse(Long chatRoomId, ParticipantStatus status);

    boolean existsByChatRoomIdAndUserIdAndStatusAndDeletedFalse(Long chatRoomId, Long userId, ParticipantStatus status);

    Optional<ChatRoomParticipant> findByChatRoomIdAndUserIdAndStatusAndDeletedFalse(Long chatRoomId, Long userId, ParticipantStatus status);

    Optional<ChatRoomParticipant> findByChatRoomIdAndUserIdAndDeletedFalse(Long chatRoomId, Long userId);

    @Query("SELECT p.chatRoom.id FROM ChatRoomParticipant p WHERE p.userId = :userId AND p.status = com.project.bangjjack.domain.chat.domain.entity.ParticipantStatus.ACTIVE AND p.deleted = false AND p.chatRoom.deleted = false")
    List<Long> findAllRoomIdsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE ChatRoomParticipant p SET p.unreadCount = p.unreadCount + 1 WHERE p.chatRoom.id = :roomId AND p.userId != :senderId AND p.status = com.project.bangjjack.domain.chat.domain.entity.ParticipantStatus.ACTIVE AND p.deleted = false")
    void incrementUnreadCountExcludingSender(@Param("roomId") Long roomId, @Param("senderId") Long senderId);
}
