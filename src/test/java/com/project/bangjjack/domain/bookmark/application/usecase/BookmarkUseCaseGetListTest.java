package com.project.bangjjack.domain.bookmark.application.usecase;

import com.project.bangjjack.domain.bookmark.application.dto.response.BookmarkedPostResponse;
import com.project.bangjjack.domain.bookmark.domain.entity.PostBookmark;
import com.project.bangjjack.domain.bookmark.domain.service.BookmarkCreateService;
import com.project.bangjjack.domain.bookmark.domain.service.BookmarkGetService;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import com.project.bangjjack.global.common.response.SliceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BookmarkUseCaseGetListTest {

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
    @DisplayName("북마크 목록 조회 시")
    class GetBookmarkedPosts {

        @Test
        @DisplayName("북마크가 있으면 BookmarkedPostResponse 목록이 반환된다")
        void 북마크_목록_반환_성공() {
            // given
            Long userId = 1L;
            Pageable pageable = PageRequest.of(0, 20);

            User user = BookmarkFixture.userWithId(userId);
            RoommatePost post = BookmarkFixture.postWithId(10L, user);
            PostBookmark bookmark = PostBookmark.create(user, post);

            given(bookmarkGetService.getBookmarkedPosts(userId, pageable))
                    .willReturn(new SliceImpl<>(List.of(bookmark), pageable, false));

            // when
            SliceResponse<BookmarkedPostResponse> result = bookmarkUseCase.getBookmarkedPosts(userId, pageable);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.hasNext()).isFalse();

            BookmarkedPostResponse response = result.content().get(0);
            assertThat(response.postId()).isEqualTo(10L);
            assertThat(response.title()).isEqualTo("룸메이트 구해요");
        }

        @Test
        @DisplayName("북마크가 없으면 빈 목록과 hasNext=false가 반환된다")
        void 북마크_없으면_빈_목록_반환() {
            // given
            Long userId = 1L;
            Pageable pageable = PageRequest.of(0, 20);

            given(bookmarkGetService.getBookmarkedPosts(userId, pageable))
                    .willReturn(new SliceImpl<>(List.of(), pageable, false));

            // when
            SliceResponse<BookmarkedPostResponse> result = bookmarkUseCase.getBookmarkedPosts(userId, pageable);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.hasNext()).isFalse();
        }

        @Test
        @DisplayName("다음 페이지가 있으면 hasNext=true가 반환된다")
        void 다음_페이지_있으면_hasNext_true() {
            // given
            Long userId = 1L;
            Pageable pageable = PageRequest.of(0, 20);

            User user = BookmarkFixture.userWithId(userId);
            RoommatePost post = BookmarkFixture.postWithId(10L, user);
            PostBookmark bookmark = PostBookmark.create(user, post);

            given(bookmarkGetService.getBookmarkedPosts(userId, pageable))
                    .willReturn(new SliceImpl<>(List.of(bookmark), pageable, true));

            // when
            SliceResponse<BookmarkedPostResponse> result = bookmarkUseCase.getBookmarkedPosts(userId, pageable);

            // then
            assertThat(result.hasNext()).isTrue();
        }
    }
}
