package com.project.bangjjack.domain.bookmark.domain.service;

import com.project.bangjjack.domain.bookmark.application.exception.BookmarkNotFoundException;
import com.project.bangjjack.domain.bookmark.domain.entity.PostBookmark;
import com.project.bangjjack.domain.bookmark.domain.repository.BookmarkRepository;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkGetService {

    private final BookmarkRepository bookmarkRepository;

    public boolean existsActiveBookmark(User user, RoommatePost post) {
        return bookmarkRepository.existsByUserAndPostAndDeletedFalse(user, post);
    }

    public PostBookmark getActiveBookmark(Long userId, Long postId) {
        return bookmarkRepository.findActiveBookmark(userId, postId)
                .orElseThrow(BookmarkNotFoundException::new);
    }

    public Slice<PostBookmark> getBookmarkedPosts(Long userId, Pageable pageable) {
        return bookmarkRepository.findBookmarkedPosts(userId, pageable);
    }
}
