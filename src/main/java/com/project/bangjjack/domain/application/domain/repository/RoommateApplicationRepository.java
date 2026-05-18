package com.project.bangjjack.domain.application.domain.repository;

import com.project.bangjjack.domain.application.domain.entity.ApplicationStatus;
import com.project.bangjjack.domain.application.domain.entity.RoommateApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoommateApplicationRepository extends JpaRepository<RoommateApplication, Long> {

    boolean existsByPostIdAndApplicantIdAndStatusAndDeletedFalse(Long postId, Long applicantId, ApplicationStatus status);

    @Query("SELECT a FROM RoommateApplication a " +
            "JOIN FETCH a.post p " +
            "JOIN FETCH p.user " +
            "JOIN FETCH a.applicant " +
            "WHERE a.id = :id AND a.deleted = false")
    Optional<RoommateApplication> findWithPostAndUserByIdAndDeletedFalse(@Param("id") Long id);
}
