package com.project.bangjjack.domain.user.application.dto.response;

import com.project.bangjjack.domain.checklist.application.dto.response.LifestyleChecklistResponse;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreferenceFactor;
import com.project.bangjjack.domain.department.domain.entity.Department;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.Semester;
import com.project.bangjjack.domain.user.domain.entity.User;

import java.util.List;

public record UserProfileResponse(
        Long userId,
        String profileImage,
        String username,
        Integer grade,
        Integer birthYear,
        String departmentName,
        Semester semester,
        Dormitory dormitory,
        List<RoommatePreferenceFactor> roommatePreferences,
        LifestyleChecklistResponse lifestyleChecklist
) {
    public static UserProfileResponse of(User user,
                                         Department department,
                                         RoommatePreference preference,
                                         LifestyleChecklistResponse lifestyleChecklist) {
        List<RoommatePreferenceFactor> preferences = preference == null
                ? null
                : List.of(
                preference.getFirstPriority(),
                preference.getSecondPriority(),
                preference.getThirdPriority()
        );

        return new UserProfileResponse(
                user.getId(),
                user.getProfileImage(),
                user.getUsername(),
                user.getGrade(),
                user.getBirthYear(),
                department == null ? null : department.getName(),
                user.getSemester(),
                user.getDormitory(),
                preferences,
                lifestyleChecklist
        );
    }
}
