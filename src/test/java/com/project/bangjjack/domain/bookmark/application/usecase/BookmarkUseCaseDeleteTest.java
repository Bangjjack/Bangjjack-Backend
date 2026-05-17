package com.project.bangjjack.domain.bookmark.application.usecase;

import com.project.bangjjack.domain.bookmark.application.exception.BookmarkNotFoundException;
import com.project.bangjjack.domain.bookmark.domain.entity.PostBookmark;
import com.project.bangjjack.domain.bookmark.domain.service.BookmarkCreateService;
import com.project.bangjjack.domain.bookmark.domain.service.BookmarkGetService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookmarkUseCaseDeleteTest {

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
    @DisplayName("북마크 해제 시")
    class DeleteBookmark {

        @Test
        @DisplayName("활성 북마크가 존재하면 softDelete가 호출되어 예외 없이 해제된다")
        void 활성_북마크_존재_시_해제_성공() {
            // given
            Long userId = 1L;
            Long postId = 10L;
            User user = BookmarkFixture.userWithId(userId);
            RoommatePost post = BookmarkFixture.postWithId(postId, user);
            PostBookmark bookmark = PostBookmark.create(user, post);

            given(bookmarkGetService.getActiveBookmark(userId, postId)).willReturn(bookmark);

            // when
            bookmarkUseCase.deleteBookmark(userId, postId);

            // then
            assertThat(bookmark.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("북마크가 존재하지 않으면 BookmarkNotFoundException이 발생한다")
        void 북마크_없으면_예외_발생() {
            // given
            Long userId = 1L;
            Long postId = 10L;

            given(bookmarkGetService.getActiveBookmark(userId, postId))
                    .willThrow(BookmarkNotFoundException.class);

            // when & then
            assertThatThrownBy(() -> bookmarkUseCase.deleteBookmark(userId, postId))
                    .isInstanceOf(BookmarkNotFoundException.class);
        }

        @Test
        @DisplayName("다른 사용자의 북마크는 조회되지 않아 BookmarkNotFoundException이 발생한다")
        void 타인_북마크_접근_시_예외_발생() {
            // given
            Long userId = 1L;
            Long otherUserId = 2L;
            Long postId = 10L;

            given(bookmarkGetService.getActiveBookmark(otherUserId, postId))
                    .willThrow(BookmarkNotFoundException.class);

            // when & then
            assertThatThrownBy(() -> bookmarkUseCase.deleteBookmark(otherUserId, postId))
                    .isInstanceOf(BookmarkNotFoundException.class);
        }
    }
}
