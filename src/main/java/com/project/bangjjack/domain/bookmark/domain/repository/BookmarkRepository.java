package com.project.bangjjack.domain.bookmark.domain.repository;

import com.project.bangjjack.domain.bookmark.domain.entity.PostBookmark;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<PostBookmark, Long> {

    boolean existsByUserAndPostAndDeletedFalse(User user, RoommatePost post);

    @Query("SELECT b FROM PostBookmark b WHERE b.user.id = :userId AND b.post.id = :postId AND b.deleted = false")
    Optional<PostBookmark> findActiveBookmark(@Param("userId") Long userId, @Param("postId") Long postId);
}