package com.project.bangjjack.domain.post.infrastructure.repository;

import com.project.bangjjack.domain.post.domain.entity.PostStatus;
import com.project.bangjjack.domain.post.domain.entity.QRoommatePost;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.repository.RoommatePostQueryRepository;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RoommatePostRepositoryImpl implements RoommatePostQueryRepository {

    private final JPAQueryFactory queryFactory;
    private static final QRoommatePost post = QRoommatePost.roommatePost;
    private static final BooleanExpression ALWAYS_FALSE = Expressions.asBoolean(true).isFalse();

    @Override
    public Slice<RoommatePost> findPostList(Campus campus, RoomSize roomSize, Pageable pageable) {
        List<RoommatePost> content = queryFactory
                .selectFrom(post)
                .where(
                        post.status.eq(PostStatus.OPEN),
                        post.deleted.eq(false),
                        campusFilter(campus),
                        roomSizeFilter(roomSize)
                )
                .orderBy(orderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.removeLast();
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private OrderSpecifier<?>[] orderSpecifiers(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            return new OrderSpecifier[]{post.createdAt.desc()};
        }
        return pageable.getSort().stream()
                .map(this::toOrderSpecifier)
                .toArray(OrderSpecifier[]::new);
    }

    private OrderSpecifier<?> toOrderSpecifier(Sort.Order order) {
        ComparableExpressionBase<?> path = switch (order.getProperty()) {
            case "createdAt" -> post.createdAt;
            case "recruitMemberCount" -> post.recruitMemberCount;
            default -> post.createdAt;
        };
        return order.isAscending() ? path.asc() : path.desc();
    }

    private BooleanExpression campusFilter(Campus campus) {
        if (campus == null) return null;
        List<Dormitory> dormitories = Dormitory.ofCampus(campus);
        if (dormitories.isEmpty()) return ALWAYS_FALSE;
        return post.dormitory.in(dormitories);
    }

    private BooleanExpression roomSizeFilter(RoomSize roomSize) {
        return roomSize != null ? post.roomSize.eq(roomSize) : null;
    }
}
