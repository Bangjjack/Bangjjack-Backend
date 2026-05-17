package com.project.bangjjack.domain.bookmark.domain.repository;

import com.project.bangjjack.domain.bookmark.domain.entity.PostBookmark;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.user.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<PostBookmark, Long> {

    boolean existsByUserAndPostAndDeletedFalse(User user, RoommatePost post);

    @Query("SELECT b FROM PostBookmark b WHERE b.user.id = :userId AND b.post.id = :postId AND b.deleted = false")
    Optional<PostBookmark> findActiveBookmark(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("SELECT b FROM PostBookmark b JOIN FETCH b.post p WHERE b.user.id = :userId AND b.deleted = false AND p.deleted = false ORDER BY b.createdAt DESC")
    Slice<PostBookmark> findBookmarkedPosts(@Param("userId") Long userId, Pageable pageable);
}