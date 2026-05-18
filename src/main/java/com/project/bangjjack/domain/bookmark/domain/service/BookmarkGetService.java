package com.project.bangjjack.domain.bookmark.domain.service;

import com.project.bangjjack.domain.bookmark.application.exception.BookmarkNotFoundException;
import com.project.bangjjack.domain.bookmark.domain.entity.PostBookmark;
import com.project.bangjjack.domain.bookmark.domain.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkGetService {

    private final BookmarkRepository bookmarkRepository;

    public boolean existsActiveBookmark(Long userId, Long postId) {
        return bookmarkRepository.existsByUserIdAndPostIdAndDeletedFalse(userId, postId);
    }

    public Optional<PostBookmark> findBookmark(Long userId, Long postId) {
        return bookmarkRepository.findFirstByUserIdAndPostIdOrderByIdDesc(userId, postId);
    }

    public PostBookmark getActiveBookmark(Long userId, Long postId) {
        return bookmarkRepository.findActiveBookmark(userId, postId)
                .orElseThrow(BookmarkNotFoundException::new);
    }

    public Slice<PostBookmark> getBookmarkedPosts(Long userId, Pageable pageable) {
        return bookmarkRepository.findBookmarkedPosts(userId,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
    }
}