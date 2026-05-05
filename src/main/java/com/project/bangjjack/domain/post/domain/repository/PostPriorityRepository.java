package com.project.bangjjack.domain.post.domain.repository;

import com.project.bangjjack.domain.post.domain.entity.PostPriority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostPriorityRepository extends JpaRepository<PostPriority, Long> {
}
