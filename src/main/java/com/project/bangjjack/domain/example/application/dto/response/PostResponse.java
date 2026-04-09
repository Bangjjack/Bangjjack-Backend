package com.project.bangjjack.domain.example.application.dto.response;

import com.project.bangjjack.domain.example.domain.entity.Post;

import java.time.LocalDateTime;

public record PostResponse(
	Long id,
	String title,
	String content,
	String authorName,
	LocalDateTime createdAt
) {
	public static PostResponse from(Post post) {
		return new PostResponse(
			post.getId(),
			post.getTitle(),
			post.getContent(),
			post.getAuthorName(),
			post.getCreatedAt()
		);
	}
}
