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

public record MyLifestyleChecklistResponse(
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
    public static MyLifestyleChecklistResponse from(ChecklistBundle bundle) {
        LifestyleChecklist checklist = bundle.checklist();
        List<SleepHabit> sleepHabits = bundle.sleepHabits().stream()
                .map(LifestyleChecklistSleepHabit::getSleepHabit)
                .toList();

        return new MyLifestyleChecklistResponse(
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
