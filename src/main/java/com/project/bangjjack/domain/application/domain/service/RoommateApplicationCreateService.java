package com.project.bangjjack.domain.application.domain.service;

import com.project.bangjjack.domain.application.domain.entity.RoommateApplication;
import com.project.bangjjack.domain.application.domain.repository.RoommateApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoommateApplicationCreateService {

    private final RoommateApplicationRepository roommateApplicationRepository;

    public RoommateApplication createApplication(RoommateApplication application) {
        return roommateApplicationRepository.save(application);
    }
}
