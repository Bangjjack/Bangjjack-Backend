package com.project.bangjjack.domain.bookmark.domain.repository;

import com.project.bangjjack.domain.bookmark.domain.entity.PostBookmark;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<PostBookmark, Long> {

    boolean existsByUserAndPostAndDeletedFalse(User user, RoommatePost post);
}