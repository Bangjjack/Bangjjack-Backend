package com.project.bangjjack.domain.post.domain.repository;

import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostSharedLifestyleRepository extends JpaRepository<PostSharedLifestyle, Long> {

    Optional<PostSharedLifestyle> findByPostAndDeletedFalse(RoommatePost post);

    @Query("SELECT psl FROM PostSharedLifestyle psl JOIN FETCH psl.post p JOIN FETCH p.user WHERE p.id = :postId AND p.deleted = false AND psl.deleted = false")
    Optional<PostSharedLifestyle> findWithPostAndUserByPostId(@Param("postId") Long postId);
}
