package com.project.bangjjack.domain.user.application.dto.response;

import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreferenceFactor;
import com.project.bangjjack.domain.checklist.domain.entity.Smoking;
import com.project.bangjjack.domain.department.domain.entity.Department;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.User;

import java.util.List;

public record AiRecommendedRoommateResponse(
        Long userId,
        String username,
        String profileImage,
        Integer birthYear,
        String departmentName,
        Dormitory dormitory,
        Smoking smoking,
        List<RoommatePreferenceFactor> roommatePreferences,
        int matchRate
) {

    public static AiRecommendedRoommateResponse from(
            User user,
            Department department,
            LifestyleChecklist checklist,
            RoommatePreference preference,
            int matchRate
    ) {
        return new AiRecommendedRoommateResponse(
                user.getId(),
                user.getUsername(),
                user.getProfileImage(),
                user.getBirthYear(),
                department.getName(),
                user.getDormitory(),
                checklist.getSmoking(),
                List.of(
                        preference.getFirstPriority(),
                        preference.getSecondPriority(),
                        preference.getThirdPriority()
                ),
                matchRate
        );
    }
}
