package com.project.bangjjack.domain.bookmark.domain.service;

import com.project.bangjjack.domain.bookmark.domain.entity.PostBookmark;
import com.project.bangjjack.domain.bookmark.domain.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkCreateService {

    private final BookmarkRepository bookmarkRepository;

    public void save(PostBookmark bookmark) {
        bookmarkRepository.save(bookmark);
    }
}
