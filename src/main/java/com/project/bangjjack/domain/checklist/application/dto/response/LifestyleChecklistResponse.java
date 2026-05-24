package com.project.bangjjack.domain.checklist.application.dto.response;

import com.project.bangjjack.domain.checklist.domain.entity.Bedtime;
import com.project.bangjjack.domain.checklist.domain.entity.CallHabit;
import com.project.bangjjack.domain.checklist.domain.entity.CleaningCycle;
import com.project.bangjjack.domain.checklist.domain.entity.DormStayTime;
import com.project.bangjjack.domain.checklist.domain.entity.IndoorTemperature;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.NoiseSensitivity;
import com.project.bangjjack.domain.checklist.domain.entity.SleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.Smoking;
import com.project.bangjjack.domain.checklist.domain.entity.WakeUpTime;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistBundle;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record LifestyleChecklistResponse(
        ChecklistField<Bedtime> bedtime,
        ChecklistField<WakeUpTime> wakeUpTime,
        ChecklistField<List<SleepHabit>> sleepHabits,
        ChecklistField<CleaningCycle> cleaningCycle,
        ChecklistField<DormStayTime> dormStayTime,
        ChecklistField<CallHabit> callHabit,
        ChecklistField<IndoorTemperature> indoorTemperature,
        ChecklistField<NoiseSensitivity> noiseSensitivity,
        ChecklistField<Smoking> smoking
) {
    public static LifestyleChecklistResponse from(ChecklistBundle bundle, ChecklistBundle viewerBundle) {
        LifestyleChecklist checklist = bundle.checklist();
        List<SleepHabit> memberHabits = toSleepHabits(bundle.sleepHabits());

        LifestyleChecklist viewerChecklist = viewerBundle == null ? null : viewerBundle.checklist();
        Set<SleepHabit> viewerHabitSet = viewerBundle == null ? null : toSleepHabitSet(viewerBundle.sleepHabits());
        boolean viewerRegistered = viewerChecklist != null;

        return new LifestyleChecklistResponse(
                ChecklistField.of(checklist.getBedtime(),
                        match(viewerRegistered, viewerRegistered ? viewerChecklist.getBedtime() : null, checklist.getBedtime())),
                ChecklistField.of(checklist.getWakeUpTime(),
                        match(viewerRegistered, viewerRegistered ? viewerChecklist.getWakeUpTime() : null, checklist.getWakeUpTime())),
                ChecklistField.of(memberHabits,
                        matchSleepHabits(viewerRegistered, viewerHabitSet, memberHabits)),
                ChecklistField.of(checklist.getCleaningCycle(),
                        match(viewerRegistered, viewerRegistered ? viewerChecklist.getCleaningCycle() : null, checklist.getCleaningCycle())),
                ChecklistField.of(checklist.getDormStayTime(),
                        match(viewerRegistered, viewerRegistered ? viewerChecklist.getDormStayTime() : null, checklist.getDormStayTime())),
                ChecklistField.of(checklist.getCallHabit(),
                        match(viewerRegistered, viewerRegistered ? viewerChecklist.getCallHabit() : null, checklist.getCallHabit())),
                ChecklistField.of(checklist.getIndoorTemperature(),
                        match(viewerRegistered, viewerRegistered ? viewerChecklist.getIndoorTemperature() : null, checklist.getIndoorTemperature())),
                ChecklistField.of(checklist.getNoiseSensitivity(),
                        match(viewerRegistered, viewerRegistered ? viewerChecklist.getNoiseSensitivity() : null, checklist.getNoiseSensitivity())),
                ChecklistField.of(checklist.getSmoking(),
                        match(viewerRegistered, viewerRegistered ? viewerChecklist.getSmoking() : null, checklist.getSmoking()))
        );
    }

    private static List<SleepHabit> toSleepHabits(List<LifestyleChecklistSleepHabit> habits) {
        return habits.stream().map(LifestyleChecklistSleepHabit::getSleepHabit).toList();
    }

    private static Set<SleepHabit> toSleepHabitSet(List<LifestyleChecklistSleepHabit> habits) {
        return habits.stream().map(LifestyleChecklistSleepHabit::getSleepHabit).collect(Collectors.toSet());
    }

    private static <T> Boolean match(boolean viewerRegistered, T viewerValue, T memberValue) {
        return viewerRegistered ? Objects.equals(viewerValue, memberValue) : null;
    }

    private static Boolean matchSleepHabits(boolean viewerRegistered, Set<SleepHabit> viewerHabitSet, List<SleepHabit> memberHabits) {
        if (!viewerRegistered) {
            return null;
        }
        return viewerHabitSet.equals(Set.copyOf(memberHabits));
    }
}
