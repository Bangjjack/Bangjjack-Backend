package com.project.bangjjack.domain.application.domain.repository;

import com.project.bangjjack.domain.application.domain.entity.ApplicationStatus;
import com.project.bangjjack.domain.application.domain.entity.RoommateApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoommateApplicationRepository extends JpaRepository<RoommateApplication, Long> {

    boolean existsByPostIdAndApplicantIdAndStatusAndDeletedFalse(Long postId, Long applicantId, ApplicationStatus status);
}
