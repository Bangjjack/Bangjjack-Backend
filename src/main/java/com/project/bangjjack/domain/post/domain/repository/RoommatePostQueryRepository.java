package com.project.bangjjack.domain.post.domain.repository;

import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface RoommatePostQueryRepository {

    Slice<RoommatePost> findPostList(Campus campus, RoomSize roomSize, Pageable pageable);
}
