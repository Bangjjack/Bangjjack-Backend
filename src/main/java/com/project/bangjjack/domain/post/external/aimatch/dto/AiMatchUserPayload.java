package com.project.bangjjack.domain.post.external.aimatch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.bangjjack.domain.checklist.domain.entity.Bedtime;
import com.project.bangjjack.domain.checklist.domain.entity.CallHabit;
import com.project.bangjjack.domain.checklist.domain.entity.CleaningCycle;
import com.project.bangjjack.domain.checklist.domain.entity.DormStayTime;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.NoiseSensitivity;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreferenceFactor;
import com.project.bangjjack.domain.checklist.domain.entity.SleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.Smoking;
import com.project.bangjjack.domain.checklist.domain.entity.WakeUpTime;
import com.project.bangjjack.domain.user.domain.entity.Gender;
import com.project.bangjjack.domain.user.domain.entity.User;

import java.util.List;

public record AiMatchUserPayload(
        @JsonProperty("gender") Gender gender,
        @JsonProperty("bedtime") Bedtime bedtime,
        @JsonProperty("wake_up_time") WakeUpTime wakeUpTime,
        @JsonProperty("call_habit") CallHabit callHabit,
        @JsonProperty("cleaning_cycle") CleaningCycle cleaningCycle,
        @JsonProperty("dorm_stay_time") DormStayTime dormStayTime,
        @JsonProperty("noise_sensitivity") NoiseSensitivity noiseSensitivity,
        @JsonProperty("smoking") Smoking smoking,
        @JsonProperty("sleep_habits") List<SleepHabit> sleepHabits,
        @JsonProperty("first_priority") RoommatePreferenceFactor firstPriority,
        @JsonProperty("second_priority") RoommatePreferenceFactor secondPriority,
        @JsonProperty("third_priority") RoommatePreferenceFactor thirdPriority
) {
    public static AiMatchUserPayload of(
            User user,
            LifestyleChecklist checklist,
            List<LifestyleChecklistSleepHabit> sleepHabits,
            RoommatePreference preference
    ) {
        return new AiMatchUserPayload(
                user.getGender(),
                checklist.getBedtime(),
                checklist.getWakeUpTime(),
                checklist.getCallHabit(),
                checklist.getCleaningCycle(),
                checklist.getDormStayTime(),
                checklist.getNoiseSensitivity(),
                checklist.getSmoking(),
                sleepHabits.stream().map(LifestyleChecklistSleepHabit::getSleepHabit).toList(),
                preference.getFirstPriority(),
                preference.getSecondPriority(),
                preference.getThirdPriority()
        );
    }
}
