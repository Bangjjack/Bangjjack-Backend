package com.project.bangjjack.domain.checklist.application.dto.request;

import com.project.bangjjack.domain.checklist.domain.entity.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record LifestyleChecklistRequest(
        @NotNull Bedtime bedtime,
        @NotNull WakeUpTime wakeUpTime,
        @NotNull @NotEmpty List<SleepHabit> sleepHabits,
        @NotNull CleaningCycle cleaningCycle,
        @NotNull DormStayTime dormStayTime,
        @NotNull CallHabit callHabit,
        @NotNull IndoorTemperature indoorTemperature,
        @NotNull NoiseSensitivity noiseSensitivity,
        @NotNull Smoking smoking
) {
}
