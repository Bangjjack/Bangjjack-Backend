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

public record LifestyleChecklistResponse(
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
    public static LifestyleChecklistResponse from(LifestyleChecklist checklist, List<LifestyleChecklistSleepHabit> sleepHabits) {
        return new LifestyleChecklistResponse(
                checklist.getBedtime(),
                checklist.getWakeUpTime(),
                sleepHabits.stream().map(LifestyleChecklistSleepHabit::getSleepHabit).toList(),
                checklist.getCleaningCycle(),
                checklist.getDormStayTime(),
                checklist.getCallHabit(),
                checklist.getIndoorTemperature(),
                checklist.getNoiseSensitivity(),
                checklist.getSmoking()
        );
    }
}
