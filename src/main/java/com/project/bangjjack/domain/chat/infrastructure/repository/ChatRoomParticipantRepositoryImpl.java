package com.project.bangjjack.domain.chat.infrastructure.repository;

import com.project.bangjjack.domain.chat.domain.entity.ChatRoomCategory;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoomParticipant;
import com.project.bangjjack.domain.chat.domain.entity.QChatRoom;
import com.project.bangjjack.domain.chat.domain.entity.QChatRoomParticipant;
import com.project.bangjjack.domain.chat.domain.repository.ChatRoomParticipantQueryRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRoomParticipantRepositoryImpl implements ChatRoomParticipantQueryRepository {

    private final JPAQueryFactory queryFactory;

    private static final QChatRoomParticipant participant = QChatRoomParticipant.chatRoomParticipant;
    private static final QChatRoom room = QChatRoom.chatRoom;

    @Override
    public List<Long> findRoomIdsByUserIdWithCursor(Long userId, Long cursorRoomId, int size, ChatRoomCategory category) {
        return queryFactory
                .select(participant.chatRoom.id)
                .from(participant)
                .where(
                        participant.userId.eq(userId),
                        participant.deleted.isFalse(),
                        participant.chatRoom.deleted.isFalse(),
                        categoryFilter(category),
                        cursorCondition(cursorRoomId)
                )
                .orderBy(participant.chatRoom.lastMessageAt.desc().nullsLast(), participant.chatRoom.id.desc())
                .limit(size + 1L)
                .fetch();
    }

    @Override
    public List<ChatRoomParticipant> findAllWithRoomByRoomIds(Collection<Long> roomIds) {
        // participant.deleted 필터 미적용: 파트너가 나간 경우에도 파트너 정보를 목록에 표시해야 함
        return queryFactory
                .selectFrom(participant)
                .join(participant.chatRoom, room).fetchJoin()
                .where(participant.chatRoom.id.in(roomIds))
                .fetch();
    }

    private BooleanExpression categoryFilter(ChatRoomCategory category) {
        return category != null ? participant.chatRoom.category.eq(category) : null;
    }

    private BooleanExpression cursorCondition(Long cursorRoomId) {
        if (cursorRoomId == null) {
            return null;
        }
        LocalDateTime cursorTime = queryFactory
                .select(room.lastMessageAt)
                .from(room)
                .where(room.id.eq(cursorRoomId))
                .fetchOne();

        if (cursorTime != null) {
            return participant.chatRoom.lastMessageAt.lt(cursorTime)
                    .or(participant.chatRoom.lastMessageAt.eq(cursorTime).and(participant.chatRoom.id.lt(cursorRoomId)))
                    .or(participant.chatRoom.lastMessageAt.isNull());
        }
        return participant.chatRoom.lastMessageAt.isNull()
                .and(participant.chatRoom.id.lt(cursorRoomId));
    }
}
