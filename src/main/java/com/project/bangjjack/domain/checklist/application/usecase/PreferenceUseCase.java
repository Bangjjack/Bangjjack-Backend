package com.project.bangjjack.domain.checklist.application.usecase;

import com.project.bangjjack.domain.checklist.application.dto.request.RoommatePreferenceRequest;
import com.project.bangjjack.domain.checklist.application.exception.AlreadyPreferenceRegisteredException;
import com.project.bangjjack.domain.checklist.application.exception.DuplicatePreferenceFactorException;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreferenceFactor;
import com.project.bangjjack.domain.checklist.domain.service.PreferenceCreateService;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PreferenceUseCase {

    private final UserGetService userGetService;
    private final PreferenceCreateService preferenceCreateService;

    @Transactional
    public void registerPreference(Long userId, RoommatePreferenceRequest request) {
        User user = userGetService.getById(userId);

        if (user.isRoommatePreferenceRegistered()) {
            throw new AlreadyPreferenceRegisteredException();
        }

        List<RoommatePreferenceFactor> preferences = request.preferences();
        if (new HashSet<>(preferences).size() != preferences.size()) {
            throw new DuplicatePreferenceFactorException();
        }

        RoommatePreference preference = RoommatePreference.create(
                user,
                preferences.get(0),
                preferences.get(1),
                preferences.get(2)
        );
        preferenceCreateService.createPreference(preference);

        user.completeRoommatePreferenceRegistration();
    }
}
