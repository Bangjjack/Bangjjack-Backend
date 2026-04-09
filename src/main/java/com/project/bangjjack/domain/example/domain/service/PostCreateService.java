package com.project.bangjjack.domain.example.domain.service;

import com.project.bangjjack.domain.example.domain.entity.Post;
import com.project.bangjjack.domain.example.domain.repository.PostRepository;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PostCreateService {

	private final PostRepository postRepository;

	public Post createPost(Post post) {
		return postRepository.save(post);
	}
}
