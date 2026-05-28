package com.project.bangjjack.domain.user.application.dto.response;

import com.project.bangjjack.domain.checklist.domain.entity.Bedtime;
import com.project.bangjjack.domain.checklist.domain.entity.CallHabit;
import com.project.bangjjack.domain.checklist.domain.entity.CleaningCycle;
import com.project.bangjjack.domain.checklist.domain.entity.DormStayTime;
import com.project.bangjjack.domain.checklist.domain.entity.IndoorTemperature;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.NoiseSensitivity;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreferenceFactor;
import com.project.bangjjack.domain.checklist.domain.entity.SleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.Smoking;
import com.project.bangjjack.domain.checklist.domain.entity.WakeUpTime;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistBundle;
import com.project.bangjjack.domain.department.domain.entity.Department;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.Gender;
import com.project.bangjjack.domain.user.domain.entity.Semester;
import com.project.bangjjack.domain.user.domain.entity.User;

import java.util.List;

public record MyProfileResponse(
        String username,
        String email,
        String profileImage,
        Integer birthYear,
        Gender gender,
        Campus campus,
        Long departmentId,
        String departmentName,
        Integer grade,
        Semester semester,
        Dormitory dormitory,
        ChecklistSummary checklist,
        List<RoommatePreferenceFactor> roommatePreferences
) {
    public static MyProfileResponse of(User user,
                                       Department department,
                                       ChecklistBundle checklistBundle,
                                       RoommatePreference preference) {
        ChecklistSummary checklistSummary = checklistBundle == null
                ? null
                : ChecklistSummary.from(checklistBundle);
        List<RoommatePreferenceFactor> preferences = preference == null
                ? null
                : List.of(
                preference.getFirstPriority(),
                preference.getSecondPriority(),
                preference.getThirdPriority()
        );

        return new MyProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.getProfileImage(),
                user.getBirthYear(),
                user.getGender(),
                user.getCampus(),
                department == null ? null : department.getId(),
                department == null ? null : department.getName(),
                user.getGrade(),
                user.getSemester(),
                user.getDormitory(),
                checklistSummary,
                preferences
        );
    }

    public record ChecklistSummary(
            Bedtime bedtime,
            WakeUpTime wakeUpTime,
            List<SleepHabit> sleepHabits,
            CleaningCycle cleaningCycle,
            DormStayTime dormStayTime,
            CallHabit callHabit,
            IndoorTemperature indoorTemperature,
            NoiseSensitivity noiseSensitivity,
            Smoking smoking
    ) {
        public static ChecklistSummary from(ChecklistBundle bundle) {
            LifestyleChecklist checklist = bundle.checklist();
            List<SleepHabit> sleepHabits = bundle.sleepHabits().stream()
                    .map(LifestyleChecklistSleepHabit::getSleepHabit)
                    .toList();

            return new ChecklistSummary(
                    checklist.getBedtime(),
                    checklist.getWakeUpTime(),
                    sleepHabits,
                    checklist.getCleaningCycle(),
                    checklist.getDormStayTime(),
                    checklist.getCallHabit(),
                    checklist.getIndoorTemperature(),
                    checklist.getNoiseSensitivity(),
                    checklist.getSmoking()
            );
        }
    }
}
