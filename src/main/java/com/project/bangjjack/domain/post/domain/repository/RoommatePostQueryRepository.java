package com.project.bangjjack.domain.post.domain.repository;

import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.domain.user.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface RoommatePostQueryRepository {

    Slice<RoommatePost> findPostList(Campus campus, RoomSize roomSize, Pageable pageable);

    List<RoommatePost> findRecommendationCandidates(User requester, RoomSize roomSize);
}
