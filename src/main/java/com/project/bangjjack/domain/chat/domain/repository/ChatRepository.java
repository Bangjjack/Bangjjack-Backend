package com.project.bangjjack.domain.chat.domain.repository;

import com.project.bangjjack.domain.chat.domain.entity.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE c.chatRoom.id = :roomId AND c.id < :cursorId AND c.deleted = false ORDER BY c.id DESC")
    List<Chat> findByCursorPage(@Param("roomId") Long roomId, @Param("cursorId") Long cursorId, Pageable pageable);

    @Query("SELECT c FROM Chat c WHERE c.chatRoom.id = :roomId AND c.id < :cursorId AND c.deleted = false AND c.createdAt > :leftAt ORDER BY c.id DESC")
    List<Chat> findByCursorPageAfterLeftAt(@Param("roomId") Long roomId, @Param("cursorId") Long cursorId, @Param("leftAt") LocalDateTime leftAt, Pageable pageable);

    @Query("SELECT c FROM Chat c WHERE c.deleted = false AND c.id IN (SELECT MAX(c2.id) FROM Chat c2 WHERE c2.chatRoom.id IN :roomIds AND c2.deleted = false GROUP BY c2.chatRoom.id)")
    List<Chat> findLastMessagesByRoomIds(@Param("roomIds") Collection<Long> roomIds);

}
