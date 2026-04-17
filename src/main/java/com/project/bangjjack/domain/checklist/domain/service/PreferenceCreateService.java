package com.project.bangjjack.domain.checklist.domain.service;

import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.repository.RoommatePreferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferenceCreateService {

    private final RoommatePreferenceRepository roommatePreferenceRepository;

    public RoommatePreference createPreference(RoommatePreference preference) {
        return roommatePreferenceRepository.save(preference);
    }
}
