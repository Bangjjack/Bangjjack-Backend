package com.project.bangjjack.domain.post.domain.repository;

import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.entity.PostStatus;
import com.project.bangjjack.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoommatePostRepository extends JpaRepository<RoommatePost, Long> {

    boolean existsByUserAndStatusAndDeletedFalse(User user, PostStatus status);
}
