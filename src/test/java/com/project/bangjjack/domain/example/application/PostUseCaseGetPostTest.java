package com.project.bangjjack.domain.example.application;

import com.project.bangjjack.domain.example.application.dto.response.PostResponse;
import com.project.bangjjack.domain.example.application.exception.PostNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PostUseCaseGetPostTest {

	@Mock
	private PostCreateService postCreateService;

	@Mock
	private PostGetService postGetService;

	@InjectMocks
	private PostUseCase postUseCase;

	@Nested
	@DisplayName("게시글 상세 조회 시")
	class GetPost {

		@Test
		@DisplayName("존재하는 id로 조회하면 해당 게시글 정보가 반환된다")
		void 존재하는_id로_조회하면_게시글_반환() {
			// given
			Post post = Post.create("제목", "내용", "작성자");
			given(postGetService.getPost(1L)).willReturn(post);

			// when
			PostResponse response = postUseCase.getPost(1L);

			// then
			assertThat(response.title()).isEqualTo("제목");
			assertThat(response.content()).isEqualTo("내용");
			assertThat(response.authorName()).isEqualTo("작성자");
		}

		@Test
		@DisplayName("존재하지 않는 id로 조회하면 POST_NOT_FOUND 예외가 발생한다")
		void 존재하지_않는_id로_조회하면_예외_발생() {
			// given
			given(postGetService.getPost(999L)).willThrow(new PostNotFoundException());

			// when & then
			assertThatThrownBy(() -> postUseCase.getPost(999L))
					.isInstanceOf(PostNotFoundException.class);
		}

		@Test
		@DisplayName("삭제된 게시글 id로 조회하면 POST_NOT_FOUND 예외가 발생한다")
		void 삭제된_게시글_조회하면_예외_발생() {
			// given
			given(postGetService.getPost(1L)).willThrow(new PostNotFoundException());

			// when & then
			assertThatThrownBy(() -> postUseCase.getPost(1L))
					.isInstanceOf(PostNotFoundException.class);
		}
	}
}
