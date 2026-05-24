package com.project.bangjjack.domain.chat.domain.repository;

import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.directRoomKey = :directRoomKey AND cr.status = com.project.bangjjack.domain.chat.domain.entity.RoomStatus.OPEN AND cr.deleted = false")
    Optional<ChatRoom> findByDirectRoomKey(@Param("directRoomKey") String directRoomKey);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.id = :id AND cr.deleted = false")
    Optional<ChatRoom> findByIdAndDeletedFalse(@Param("id") Long id);

    boolean existsByIdAndDeletedFalse(Long id);
}
