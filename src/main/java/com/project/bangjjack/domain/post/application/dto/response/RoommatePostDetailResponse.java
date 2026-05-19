package com.project.bangjjack.domain.post.application.dto.response;

import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.PostStatus;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.Semester;

import java.time.LocalDateTime;

public record RoommatePostDetailResponse(
        Long postId,
        String title,
        String description,
        RoomSize roomSize,
        int recruitMemberCount,
        PostStatus status,
        Semester semester,
        Dormitory dormitory,
        boolean isOwner,
        boolean isBookmarked,
        LocalDateTime createdAt,
        AuthorResponse author,
        SharedLifestyleResponse sharedLifestyle,
        RoommatePreferenceResponse roommatePreference
) {
    public static RoommatePostDetailResponse from(
            RoommatePost post,
            PostSharedLifestyle sharedLifestyle,
            boolean isOwner,
            boolean isBookmarked,
            RoommatePreferenceResponse roommatePreference
    ) {
        return new RoommatePostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getRoomSize(),
                post.getRecruitMemberCount(),
                post.getStatus(),
                post.getSemester(),
                post.getDormitory(),
                isOwner,
                isBookmarked,
                post.getCreatedAt(),
                AuthorResponse.from(post.getUser()),
                SharedLifestyleResponse.from(sharedLifestyle),
                roommatePreference
        );
    }
}
