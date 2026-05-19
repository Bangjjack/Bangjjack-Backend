package com.project.bangjjack.domain.roommategroup.domain.repository;

import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroup;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoommateGroupRepository extends JpaRepository<RoommateGroup, Long> {

    Optional<RoommateGroup> findByPostIdAndDeletedFalse(Long postId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM RoommateGroup g WHERE g.post.id = :postId AND g.deleted = false")
    Optional<RoommateGroup> findByPostIdAndDeletedFalseForUpdate(@Param("postId") Long postId);
}
