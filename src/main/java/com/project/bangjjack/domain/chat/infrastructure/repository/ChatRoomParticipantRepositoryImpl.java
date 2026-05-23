package com.project.bangjjack.domain.chat.infrastructure.repository;

import com.project.bangjjack.domain.chat.domain.entity.ChatRoomCategory;
import com.project.bangjjack.domain.chat.domain.entity.ChatRoomParticipant;
import com.project.bangjjack.domain.chat.domain.entity.QChatRoom;
import com.project.bangjjack.domain.chat.domain.entity.QChatRoomParticipant;
import com.project.bangjjack.domain.chat.domain.entity.RoomType;
import com.project.bangjjack.domain.chat.domain.repository.ChatRoomParticipantQueryRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ChatRoomParticipantRepositoryImpl implements ChatRoomParticipantQueryRepository {

    private final JPAQueryFactory queryFactory;

    private static final QChatRoomParticipant participant = QChatRoomParticipant.chatRoomParticipant;
    private static final QChatRoom room = QChatRoom.chatRoom;

    @Override
    public List<ChatRoomParticipant> findMyDirectParticipantsWithCursor(Long userId, Long cursorRoomId, LocalDateTime cursorLastMessageAt, int size, ChatRoomCategory category) {
        return queryFactory
                .selectFrom(participant)
                .join(participant.chatRoom, room).fetchJoin()
                .where(
                        participant.userId.eq(userId),
                        participant.deleted.isFalse(),
                        participant.chatRoom.deleted.isFalse(),
                        participant.chatRoom.roomType.eq(RoomType.DIRECT),
                        categoryFilter(category),
                        cursorCondition(cursorRoomId, cursorLastMessageAt)
                )
                .orderBy(participant.chatRoom.lastMessageAt.desc().nullsLast(), participant.chatRoom.id.desc())
                .limit(size + 1L)
                .fetch();
    }

    @Override
    public Map<Long, Long> findPartnerIdsByDirectRoomIds(Collection<Long> roomIds, Long myUserId) {
        if (roomIds.isEmpty()) {
            return Map.of();
        }

        // participant.deleted 필터 미적용: 파트너가 나간 경우에도 파트너 정보를 목록에 표시해야 함
        return queryFactory
                .select(participant.chatRoom.id, participant.userId)
                .from(participant)
                .where(
                        participant.chatRoom.id.in(roomIds),
                        participant.chatRoom.roomType.eq(RoomType.DIRECT),
                        participant.userId.ne(myUserId)
                )
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(participant.chatRoom.id),
                        tuple -> tuple.get(participant.userId)
                ));
    }

    private BooleanExpression categoryFilter(ChatRoomCategory category) {
        return category != null ? participant.chatRoom.category.eq(category) : null;
    }

    private BooleanExpression cursorCondition(Long cursorRoomId, LocalDateTime cursorLastMessageAt) {
        if (cursorRoomId == null) {
            return null;
        }
        if (cursorLastMessageAt != null) {
            return participant.chatRoom.lastMessageAt.lt(cursorLastMessageAt)
                    .or(participant.chatRoom.lastMessageAt.eq(cursorLastMessageAt)
                            .and(participant.chatRoom.id.lt(cursorRoomId)))
                    .or(participant.chatRoom.lastMessageAt.isNull());
        } else {
            return participant.chatRoom.lastMessageAt.isNull()
                    .and(participant.chatRoom.id.lt(cursorRoomId));
        }
    }
}
