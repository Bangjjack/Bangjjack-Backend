package com.project.bangjjack.domain.post.application.dto.response;

import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;

import java.time.LocalDateTime;

public record RoommatePostSummaryResponse(
        Long postId,
        String title,
        String description,
        Dormitory dormitory,
        RoomSize roomSize,
        int recruitMemberCount,
        LocalDateTime createdAt
) {
    public static RoommatePostSummaryResponse from(RoommatePost post) {
        return new RoommatePostSummaryResponse(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getDormitory(),
                post.getRoomSize(),
                post.getRecruitMemberCount(),
                post.getCreatedAt()
        );
    }
}
