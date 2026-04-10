package com.project.bangjjack.domain.example.application;

import com.project.bangjjack.domain.example.application.dto.request.CreatePostRequest;
import com.project.bangjjack.domain.example.application.dto.response.PostResponse;
import com.project.bangjjack.domain.example.application.usecase.PostUseCase;
import com.project.bangjjack.domain.example.domain.entity.Post;
import com.project.bangjjack.domain.example.domain.service.PostCreateService;
import com.project.bangjjack.domain.example.domain.service.PostGetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PostUseCaseCreatePostTest {

	@Mock
	private PostCreateService postCreateService;

	@Mock
	private PostGetService postGetService;

	@InjectMocks
	private PostUseCase postUseCase;

	@Nested
	@DisplayName("게시글 생성 시")
	class CreatePost {

		@Test
		@DisplayName("유효한 요청으로 게시글을 생성하면 저장된 게시글 정보가 반환된다")
		void 유효한_요청으로_게시글_생성_성공() {
			// given
			CreatePostRequest request = new CreatePostRequest("제목", "내용", "작성자");
			Post post = Post.create("제목", "내용", "작성자");
			given(postCreateService.createPost(any(Post.class))).willReturn(post);

			// when
			PostResponse response = postUseCase.createPost(request);

			// then
			assertThat(response.title()).isEqualTo("제목");
			assertThat(response.content()).isEqualTo("내용");
			assertThat(response.authorName()).isEqualTo("작성자");
		}

		@Test
		@DisplayName("title이 정확히 100자인 경우 정상 생성된다")
		void title이_정확히_100자인_경우_정상_생성() {
			// given
			String maxTitle = "가".repeat(100);
			CreatePostRequest request = new CreatePostRequest(maxTitle, "내용", "작성자");
			Post post = Post.create(maxTitle, "내용", "작성자");
			given(postCreateService.createPost(any(Post.class))).willReturn(post);

			// when
			PostResponse response = postUseCase.createPost(request);

			// then
			assertThat(response.title()).hasSize(100);
		}

		@Test
		@DisplayName("content가 정확히 2000자인 경우 정상 생성된다")
		void content가_정확히_2000자인_경우_정상_생성() {
			// given
			String maxContent = "가".repeat(2000);
			CreatePostRequest request = new CreatePostRequest("제목", maxContent, "작성자");
			Post post = Post.create("제목", maxContent, "작성자");
			given(postCreateService.createPost(any(Post.class))).willReturn(post);

			// when
			PostResponse response = postUseCase.createPost(request);

			// then
			assertThat(response.content()).hasSize(2000);
		}
	}
}
