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

import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    public static LifestyleChecklistResponse from(
            LifestyleChecklist checklist,
            List<LifestyleChecklistSleepHabit> sleepHabits,
            LifestyleChecklist viewerChecklist,
            List<LifestyleChecklistSleepHabit> viewerSleepHabits
    ) {
        List<SleepHabit> memberHabits = sleepHabits.stream().map(LifestyleChecklistSleepHabit::getSleepHabit).toList();
        boolean viewerRegistered = viewerChecklist != null;

        return new LifestyleChecklistResponse(
                ChecklistField.of(checklist.getBedtime(),
                        match(viewerRegistered, viewerChecklist == null ? null : viewerChecklist.getBedtime(), checklist.getBedtime())),
                ChecklistField.of(checklist.getWakeUpTime(),
                        match(viewerRegistered, viewerChecklist == null ? null : viewerChecklist.getWakeUpTime(), checklist.getWakeUpTime())),
                ChecklistField.of(memberHabits,
                        matchSleepHabits(viewerRegistered, viewerSleepHabits, memberHabits)),
                ChecklistField.of(checklist.getCleaningCycle(),
                        match(viewerRegistered, viewerChecklist == null ? null : viewerChecklist.getCleaningCycle(), checklist.getCleaningCycle())),
                ChecklistField.of(checklist.getDormStayTime(),
                        match(viewerRegistered, viewerChecklist == null ? null : viewerChecklist.getDormStayTime(), checklist.getDormStayTime())),
                ChecklistField.of(checklist.getCallHabit(),
                        match(viewerRegistered, viewerChecklist == null ? null : viewerChecklist.getCallHabit(), checklist.getCallHabit())),
                ChecklistField.of(checklist.getIndoorTemperature(),
                        match(viewerRegistered, viewerChecklist == null ? null : viewerChecklist.getIndoorTemperature(), checklist.getIndoorTemperature())),
                ChecklistField.of(checklist.getNoiseSensitivity(),
                        match(viewerRegistered, viewerChecklist == null ? null : viewerChecklist.getNoiseSensitivity(), checklist.getNoiseSensitivity())),
                ChecklistField.of(checklist.getSmoking(),
                        match(viewerRegistered, viewerChecklist == null ? null : viewerChecklist.getSmoking(), checklist.getSmoking()))
        );
    }

    private static <T> Boolean match(boolean viewerRegistered, T viewerValue, T memberValue) {
        return viewerRegistered ? Objects.equals(viewerValue, memberValue) : null;
    }

    private static Boolean matchSleepHabits(boolean viewerRegistered, List<LifestyleChecklistSleepHabit> viewerHabits, List<SleepHabit> memberHabits) {
        if (!viewerRegistered) {
            return null;
        }
        Set<SleepHabit> viewerSet = viewerHabits.stream().map(LifestyleChecklistSleepHabit::getSleepHabit).collect(java.util.stream.Collectors.toSet());
        Set<SleepHabit> memberSet = Set.copyOf(memberHabits);
        return viewerSet.equals(memberSet);
    }
}
