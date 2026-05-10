package com.project.bangjjack.domain.checklist.domain.service;

import com.project.bangjjack.domain.checklist.application.exception.ChecklistNotFoundException;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.repository.RoommatePreferenceRepository;
import com.project.bangjjack.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoommatePreferenceGetService {

    private final RoommatePreferenceRepository roommatePreferenceRepository;

    public RoommatePreference getByUser(User user) {
        return roommatePreferenceRepository.findByUserAndDeletedFalse(user)
                .orElseThrow(ChecklistNotFoundException::new);
    }
}
