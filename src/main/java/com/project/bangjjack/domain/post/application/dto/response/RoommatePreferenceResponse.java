package com.project.bangjjack.domain.post.application.dto.response;

import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreferenceFactor;

public record RoommatePreferenceResponse(
        RoommatePreferenceFactor firstPriority,
        RoommatePreferenceFactor secondPriority,
        RoommatePreferenceFactor thirdPriority
) {
    public static RoommatePreferenceResponse from(RoommatePreference preference) {
        return new RoommatePreferenceResponse(
                preference.getFirstPriority(),
                preference.getSecondPriority(),
                preference.getThirdPriority()
        );
    }
}
