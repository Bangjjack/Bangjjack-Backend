package com.project.bangjjack.domain.post.domain.repository;

import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.entity.PostStatus;
import com.project.bangjjack.domain.user.domain.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoommatePostRepository extends JpaRepository<RoommatePost, Long>, RoommatePostQueryRepository {

    boolean existsByUserAndStatusAndDeletedFalse(User user, PostStatus status);

    @Query("SELECT p FROM RoommatePost p JOIN FETCH p.user WHERE p.id = :postId AND p.deleted = false")
    Optional<RoommatePost> findWithUserByIdAndDeletedFalse(@Param("postId") Long postId);

    @Query("SELECT p FROM RoommatePost p JOIN FETCH p.user WHERE p.id = :postId AND p.status = :status AND p.deleted = false")
    Optional<RoommatePost> findWithUserByIdAndStatusAndDeletedFalse(@Param("postId") Long postId, @Param("status") PostStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM RoommatePost p WHERE p.id = :postId AND p.deleted = false")
    Optional<RoommatePost> findByIdAndDeletedFalseForUpdate(@Param("postId") Long postId);
}
