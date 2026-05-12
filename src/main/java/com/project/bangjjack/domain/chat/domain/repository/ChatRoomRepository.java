package com.project.bangjjack.domain.chat.domain.repository;

import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.directRoomKey = :directRoomKey AND cr.deleted = false")
    Optional<ChatRoom> findByDirectRoomKey(@Param("directRoomKey") String directRoomKey);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.id = :id AND cr.deleted = false")
    Optional<ChatRoom> findByIdAndDeletedFalse(@Param("id") Long id);

    @Query("SELECT COUNT(cr) > 0 FROM ChatRoom cr " +
            "JOIN ChatRoomParticipant crp ON cr.id = crp.chatRoom.id " +
            "WHERE cr.id = :roomId AND crp.userId = :userId " +
            "AND cr.deleted = false AND crp.deleted = false")
    boolean existsActiveRoomWithParticipant(@Param("roomId") Long roomId, @Param("userId") Long userId);
}
