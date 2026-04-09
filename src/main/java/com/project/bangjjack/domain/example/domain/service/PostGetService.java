package com.project.bangjjack.domain.example.domain.service;

import com.project.bangjjack.domain.example.application.exception.PostNotFoundException;
import com.project.bangjjack.domain.example.domain.entity.Post;
import com.project.bangjjack.domain.example.domain.repository.PostRepository;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostGetService {

	private final PostRepository postRepository;

	public List<Post> getAllPosts() {
		return postRepository.findAllByDeletedFalse();
	}

	public Post getPost(Long id) {
		return postRepository.findByIdAndDeletedFalse(id)
			.orElseThrow(PostNotFoundException::new);
	}
}
