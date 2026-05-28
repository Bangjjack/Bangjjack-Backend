package com.project.bangjjack.domain.post.application.dto.response;

import com.project.bangjjack.domain.post.domain.entity.PostStatus;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;

public record MyRoommatePostResponse(
        Long postId,
        String title,
        String description,
        Dormitory dormitory,
        RoomSize roomSize,
        int totalMemberCount,
        int currentMemberCount,
        boolean isClosed
) {
    public static MyRoommatePostResponse from(RoommatePost post, long currentMemberCount) {
        return new MyRoommatePostResponse(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getDormitory(),
                post.getRoomSize(),
                post.getRecruitMemberCount() + 1,
                (int) currentMemberCount,
                post.getStatus() == PostStatus.CLOSED
        );
    }
}
