package com.project.bangjjack.domain.checklist.domain.service;

import com.project.bangjjack.domain.checklist.application.exception.ChecklistNotFoundException;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.repository.RoommatePreferenceRepository;
import com.project.bangjjack.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoommatePreferenceGetService {

    private final RoommatePreferenceRepository roommatePreferenceRepository;

    public RoommatePreference getByUser(User user) {
        return roommatePreferenceRepository.findByUserAndDeletedFalse(user)
                .orElseThrow(ChecklistNotFoundException::new);
    }

    public Optional<RoommatePreference> findByUser(User user) {
        return roommatePreferenceRepository.findByUserAndDeletedFalse(user);
    }

    public Map<Long, RoommatePreference> getByUserIdIn(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return roommatePreferenceRepository.findAllByUserIdInAndDeletedFalse(userIds).stream()
                .collect(Collectors.toMap(
                        preference -> preference.getUser().getId(),
                        preference -> preference
                ));
    }
}
