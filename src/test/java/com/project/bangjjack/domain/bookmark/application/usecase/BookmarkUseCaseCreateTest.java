package com.project.bangjjack.domain.bookmark.application.usecase;

import com.project.bangjjack.domain.bookmark.application.exception.AlreadyBookmarkedException;
import com.project.bangjjack.domain.bookmark.domain.service.BookmarkCreateService;
import com.project.bangjjack.domain.bookmark.domain.service.BookmarkGetService;
import com.project.bangjjack.domain.post.application.exception.PostNotFoundException;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class BookmarkUseCaseCreateTest {

    @Mock
    private UserGetService userGetService;

    @Mock
    private RoommatePostGetService roommatePostGetService;

    @Mock
    private BookmarkGetService bookmarkGetService;

    @Mock
    private BookmarkCreateService bookmarkCreateService;

    @InjectMocks
    private BookmarkUseCase bookmarkUseCase;

    @Nested
    @DisplayName("북마크 등록 시")
    class CreateBookmark {

        @Test
        @DisplayName("유효한 요청이면 예외 없이 북마크가 저장된다")
        void 유효한_요청_북마크_저장_성공() {
            // given
            Long userId = 1L;
            Long postId = 10L;
            User user = BookmarkFixture.userWithId(userId);
            RoommatePost post = BookmarkFixture.postWithId(postId, user);

            given(userGetService.getById(userId)).willReturn(user);
            given(roommatePostGetService.getById(postId)).willReturn(post);
            given(bookmarkGetService.existsActiveBookmark(user, post)).willReturn(false);

            // when & then
            assertThatCode(() -> bookmarkUseCase.createBookmark(userId, postId))
                    .doesNotThrowAnyException();

            then(bookmarkCreateService).should().save(any());
        }

        @Test
        @DisplayName("존재하지 않는 게시글이면 PostNotFoundException이 발생한다")
        void 존재하지_않는_게시글_예외_발생() {
            // given
            Long userId = 1L;
            Long postId = 99L;
            User user = BookmarkFixture.userWithId(userId);

            given(userGetService.getById(userId)).willReturn(user);
            given(roommatePostGetService.getById(postId)).willThrow(PostNotFoundException.class);

            // when & then
            assertThatThrownBy(() -> bookmarkUseCase.createBookmark(userId, postId))
                    .isInstanceOf(PostNotFoundException.class);

            then(bookmarkCreateService).should(never()).save(any());
        }

        @Test
        @DisplayName("이미 북마크한 게시글이면 AlreadyBookmarkedException이 발생한다")
        void 이미_북마크한_게시글_예외_발생() {
            // given
            Long userId = 1L;
            Long postId = 10L;
            User user = BookmarkFixture.userWithId(userId);
            RoommatePost post = BookmarkFixture.postWithId(postId, user);

            given(userGetService.getById(userId)).willReturn(user);
            given(roommatePostGetService.getById(postId)).willReturn(post);
            given(bookmarkGetService.existsActiveBookmark(user, post)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> bookmarkUseCase.createBookmark(userId, postId))
                    .isInstanceOf(AlreadyBookmarkedException.class);

            then(bookmarkCreateService).should(never()).save(any());
        }
    }
}
