package com.project.bangjjack.domain.post.domain.repository;

import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostSharedLifestyleRepository extends JpaRepository<PostSharedLifestyle, Long> {

    Optional<PostSharedLifestyle> findByPost(RoommatePost post);
}
