package com.project.bangjjack.domain.chat.domain.repository;

import com.project.bangjjack.domain.chat.domain.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr LEFT JOIN FETCH cr.participants WHERE cr.directRoomKey = :directRoomKey AND cr.deleted = false")
    Optional<ChatRoom> findByDirectRoomKeyWithParticipants(@Param("directRoomKey") String directRoomKey);
}
