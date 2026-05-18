package com.project.bangjjack.domain.roommategroup.domain.repository;

import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoommateGroupRepository extends JpaRepository<RoommateGroup, Long> {

    Optional<RoommateGroup> findByPostIdAndDeletedFalse(Long postId);
}
