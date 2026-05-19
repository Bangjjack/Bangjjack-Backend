package com.project.bangjjack.domain.user.application.dto.response;

import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreferenceFactor;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.Semester;
import com.project.bangjjack.domain.user.domain.entity.User;

import java.util.List;

public record UserBasicTagResponse(
        Semester semester,
        Dormitory dormitory,
        List<RoommatePreferenceFactor> roommatePreferences
) {
    public static UserBasicTagResponse of(User user, RoommatePreference preference) {
        return new UserBasicTagResponse(
                user.getSemester(),
                user.getDormitory(),
                List.of(
                        preference.getFirstPriority(),
                        preference.getSecondPriority(),
                        preference.getThirdPriority()
                )
        );
    }
}
