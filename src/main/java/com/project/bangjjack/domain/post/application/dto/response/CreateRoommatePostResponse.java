package com.project.bangjjack.domain.post.application.dto.response;

public record CreateRoommatePostResponse(Long postId) {

    public static CreateRoommatePostResponse from(Long postId) {
        return new CreateRoommatePostResponse(postId);
    }
}