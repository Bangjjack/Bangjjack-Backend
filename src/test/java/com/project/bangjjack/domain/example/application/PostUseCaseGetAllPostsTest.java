package com.project.bangjjack.domain.example.application;

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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PostUseCaseGetAllPostsTest {

	@Mock
	private PostCreateService postCreateService;

	@Mock
	private PostGetService postGetService;

	@InjectMocks
	private PostUseCase postUseCase;

	@Nested
	@DisplayName("전체 게시글 조회 시")
	class GetAllPosts {

		@Test
		@DisplayName("게시글이 존재하면 전체 목록이 반환된다")
		void 게시글이_존재하면_전체_목록_반환() {
			// given
			List<Post> posts = List.of(
					Post.create("제목1", "내용1", "작성자1"),
					Post.create("제목2", "내용2", "작성자2")
			);
			given(postGetService.getAllPosts()).willReturn(posts);

			// when
			List<PostResponse> result = postUseCase.getAllPosts();

			// then
			assertThat(result).hasSize(2);
			assertThat(result.get(0).title()).isEqualTo("제목1");
			assertThat(result.get(1).title()).isEqualTo("제목2");
		}

		@Test
		@DisplayName("게시글이 없으면 빈 리스트가 반환된다")
		void 게시글이_없으면_빈_리스트_반환() {
			// given
			given(postGetService.getAllPosts()).willReturn(List.of());

			// when
			List<PostResponse> result = postUseCase.getAllPosts();

			// then
			assertThat(result).isEmpty();
		}
	}
}
