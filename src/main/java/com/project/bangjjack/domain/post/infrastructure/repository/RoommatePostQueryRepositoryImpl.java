package com.project.bangjjack.domain.post.infrastructure.repository;

import com.project.bangjjack.domain.post.domain.entity.PostStatus;
import com.project.bangjjack.domain.post.domain.entity.QRoommatePost;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.repository.RoommatePostQueryRepository;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RoommatePostQueryRepositoryImpl implements RoommatePostQueryRepository {

    private final JPAQueryFactory queryFactory;
    private static final QRoommatePost post = QRoommatePost.roommatePost;

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
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(content.size() - 1);
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression campusFilter(Campus campus) {
        if (campus == null) return null;
        return post.dormitory.in(Dormitory.ofCampus(campus));
    }

    private BooleanExpression roomSizeFilter(RoomSize roomSize) {
        return roomSize != null ? post.roomSize.eq(roomSize) : null;
    }
}
