package com.project.bangjjack.domain.application.domain.service;

import com.project.bangjjack.domain.application.application.exception.ApplicationNotFoundException;
import com.project.bangjjack.domain.application.domain.entity.ApplicationStatus;
import com.project.bangjjack.domain.application.domain.entity.RoommateApplication;
import com.project.bangjjack.domain.application.domain.repository.RoommateApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoommateApplicationGetService {

    private final RoommateApplicationRepository roommateApplicationRepository;

    public boolean existsPendingByPostIdAndApplicantId(Long postId, Long applicantId) {
        return roommateApplicationRepository.existsByPostIdAndApplicantIdAndStatusAndDeletedFalse(
                postId, applicantId, ApplicationStatus.PENDING);
    }

    public RoommateApplication getWithPostAndUserById(Long applicationId) {
        return roommateApplicationRepository.findWithPostAndUserByIdAndDeletedFalse(applicationId)
                .orElseThrow(ApplicationNotFoundException::new);
    }
}
