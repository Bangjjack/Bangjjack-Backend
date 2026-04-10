package com.project.bangjjack.domain.example.presentation;

import com.project.bangjjack.domain.example.application.dto.request.CreatePostRequest;
import com.project.bangjjack.domain.example.application.dto.response.PostResponse;
import com.project.bangjjack.domain.example.application.usecase.PostUseCase;
import com.project.bangjjack.domain.example.presentation.response.PostResponseCode;
import com.project.bangjjack.global.common.response.CommonResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

	private final PostUseCase postUseCase;

	@PostMapping
	public CommonResponse<PostResponse> createPost(@RequestBody @Valid CreatePostRequest request) {
		PostResponse response = postUseCase.createPost(request);
		return CommonResponse.success(PostResponseCode.POST_CREATED, response);
	}

	@GetMapping
	public CommonResponse<List<PostResponse>> getAllPosts() {
		List<PostResponse> response = postUseCase.getAllPosts();
		return CommonResponse.success(PostResponseCode.POSTS_FOUND, response);
	}

	@GetMapping("/{postId}")
	public CommonResponse<PostResponse> getPost(@PathVariable Long postId) {
		PostResponse response = postUseCase.getPost(postId);
		return CommonResponse.success(PostResponseCode.POST_FOUND, response);
	}
}
