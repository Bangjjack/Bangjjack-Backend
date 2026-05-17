package com.project.bangjjack.domain.bookmark.application.usecase;

import com.project.bangjjack.domain.bookmark.application.dto.response.BookmarkedPostResponse;
import com.project.bangjjack.domain.bookmark.application.exception.AlreadyBookmarkedException;
import com.project.bangjjack.domain.bookmark.domain.entity.PostBookmark;
import com.project.bangjjack.domain.bookmark.domain.service.BookmarkCreateService;
import com.project.bangjjack.domain.bookmark.domain.service.BookmarkGetService;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import com.project.bangjjack.global.common.response.SliceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkUseCase {

    private final UserGetService userGetService;
    private final RoommatePostGetService roommatePostGetService;
    private final BookmarkGetService bookmarkGetService;
    private final BookmarkCreateService bookmarkCreateService;

    @Transactional
    public void createBookmark(Long userId, Long postId) {
        User user = userGetService.getById(userId);
        RoommatePost post = roommatePostGetService.getById(postId);

        if (bookmarkGetService.existsActiveBookmark(user, post)) {
            throw new AlreadyBookmarkedException();
        }

        bookmarkCreateService.save(PostBookmark.create(user, post));
    }

    @Transactional
    public void deleteBookmark(Long userId, Long postId) {
        PostBookmark bookmark = bookmarkGetService.getActiveBookmark(userId, postId);
        bookmark.softDelete();
    }

    public SliceResponse<BookmarkedPostResponse> getBookmarkedPosts(Long userId, Pageable pageable) {
        return SliceResponse.from(
                bookmarkGetService.getBookmarkedPosts(userId, pageable)
                        .map(BookmarkedPostResponse::from)
        );
    }
}
