package com.project.bangjjack.domain.chat.domain.repository;

import com.project.bangjjack.domain.chat.domain.entity.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE c.chatRoom.id = :roomId AND c.id < :cursorId AND c.deleted = false ORDER BY c.id DESC")
    List<Chat> findByCursorPage(@Param("roomId") Long roomId, @Param("cursorId") Long cursorId, Pageable pageable);
}
