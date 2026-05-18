package com.project.bangjjack.domain.bookmark.application.dto.response;

import com.project.bangjjack.domain.bookmark.domain.entity.PostBookmark;
import com.project.bangjjack.domain.post.domain.entity.PostStatus;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;

import java.time.LocalDateTime;

public record BookmarkedPostResponse(
        Long postId,
        String title,
        String description,
        Dormitory dormitory,
        RoomSize roomSize,
        int recruitMemberCount,
        PostStatus status,
        LocalDateTime bookmarkedAt
) {
    public static BookmarkedPostResponse from(PostBookmark bookmark) {
        RoommatePost post = bookmark.getPost();
        return new BookmarkedPostResponse(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getDormitory(),
                post.getRoomSize(),
                post.getRecruitMemberCount(),
                post.getStatus(),
                bookmark.getUpdatedAt()
        );
    }
}
