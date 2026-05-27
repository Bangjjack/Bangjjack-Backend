package com.project.bangjjack.domain.post.application.dto.response;

import com.project.bangjjack.domain.checklist.domain.entity.Smoking;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;

import java.time.LocalDateTime;

public record AiRecommendedPostResponse(
        Long postId,
        String title,
        String description,
        Dormitory dormitory,
        RoomSize roomSize,
        Smoking smoking,
        int currentMemberCount,
        int totalMemberCount,
        int matchRate,
        LocalDateTime createdAt
) {

    public static AiRecommendedPostResponse from(RoommatePost post, Smoking smoking, int currentMemberCount, int matchRate) {
        return new AiRecommendedPostResponse(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getDormitory(),
                post.getRoomSize(),
                smoking,
                currentMemberCount,
                post.getRoomSize().getTotalMemberCount(),
                matchRate,
                post.getCreatedAt()
        );
    }
}
