package com.project.bangjjack.domain.post.application.dto.response;

import com.project.bangjjack.domain.user.domain.entity.User;

public record AuthorResponse(
        Long authorId,
        String username,
        String profileImage
) {
    public static AuthorResponse from(User user) {
        return new AuthorResponse(user.getId(), user.getUsername(), user.getProfileImage());
    }
}
