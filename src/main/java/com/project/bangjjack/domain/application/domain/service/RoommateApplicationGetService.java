package com.project.bangjjack.domain.application.domain.service;

import com.project.bangjjack.domain.application.domain.entity.ApplicationStatus;
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
}
