package com.project.bangjjack.domain.bookmark.application.usecase;

import com.project.bangjjack.domain.bookmark.application.exception.AlreadyBookmarkedException;
import com.project.bangjjack.domain.bookmark.domain.entity.PostBookmark;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
        @DisplayName("기존 북마크가 없으면 새로 생성된다")
        void 기존_북마크_없으면_새로_생성() {
            Long userId = 1L;
            Long postId = 10L;
            User user = BookmarkFixture.userWithId(userId);
            RoommatePost post = BookmarkFixture.postWithId(postId, user);

            given(bookmarkGetService.findBookmark(userId, postId)).willReturn(Optional.empty());
            given(userGetService.getById(userId)).willReturn(user);
            given(roommatePostGetService.getById(postId)).willReturn(post);

            assertThatCode(() -> bookmarkUseCase.createBookmark(userId, postId))
                    .doesNotThrowAnyException();

            then(bookmarkCreateService).should().save(any());
        }

        @Test
        @DisplayName("soft delete된 북마크가 있으면 재활성화한다")
        void soft_delete된_북마크_재활성화() {
            Long userId = 1L;
            Long postId = 10L;
            User user = BookmarkFixture.userWithId(userId);
            RoommatePost post = BookmarkFixture.postWithId(postId, user);
            PostBookmark deletedBookmark = BookmarkFixture.deletedBookmarkWithId(1L, user, post);

            given(bookmarkGetService.findBookmark(userId, postId)).willReturn(Optional.of(deletedBookmark));

            assertThatCode(() -> bookmarkUseCase.createBookmark(userId, postId))
                    .doesNotThrowAnyException();

            assertThat(deletedBookmark.isDeleted()).isFalse();
            then(bookmarkCreateService).should(never()).save(any());
        }

        @Test
        @DisplayName("이미 활성 북마크가 있으면 AlreadyBookmarkedException이 발생한다")
        void 이미_활성_북마크_예외_발생() {
            Long userId = 1L;
            Long postId = 10L;
            User user = BookmarkFixture.userWithId(userId);
            RoommatePost post = BookmarkFixture.postWithId(postId, user);
            PostBookmark activeBookmark = BookmarkFixture.activeBookmarkWithId(1L, user, post);

            given(bookmarkGetService.findBookmark(userId, postId)).willReturn(Optional.of(activeBookmark));

            assertThatThrownBy(() -> bookmarkUseCase.createBookmark(userId, postId))
                    .isInstanceOf(AlreadyBookmarkedException.class);

            then(bookmarkCreateService).should(never()).save(any());
        }

        @Test
        @DisplayName("존재하지 않는 게시글이면 PostNotFoundException이 발생한다")
        void 존재하지_않는_게시글_예외_발생() {
            Long userId = 1L;
            Long postId = 99L;
            User user = BookmarkFixture.userWithId(userId);

            given(bookmarkGetService.findBookmark(userId, postId)).willReturn(Optional.empty());
            given(userGetService.getById(userId)).willReturn(user);
            given(roommatePostGetService.getById(postId)).willThrow(PostNotFoundException.class);

            assertThatThrownBy(() -> bookmarkUseCase.createBookmark(userId, postId))
                    .isInstanceOf(PostNotFoundException.class);

            then(bookmarkCreateService).should(never()).save(any());
        }
    }
}
