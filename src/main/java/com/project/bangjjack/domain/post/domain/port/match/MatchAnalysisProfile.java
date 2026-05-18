package com.project.bangjjack.domain.post.domain.port.match;

import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.user.domain.entity.User;

import java.util.List;

public record MatchAnalysisProfile(
        User user,
        LifestyleChecklist checklist,
        List<LifestyleChecklistSleepHabit> sleepHabits,
        RoommatePreference preference
) {
    public static MatchAnalysisProfile of(
            User user,
            LifestyleChecklist checklist,
            List<LifestyleChecklistSleepHabit> sleepHabits,
            RoommatePreference preference
    ) {
        return new MatchAnalysisProfile(user, checklist, sleepHabits, preference);
    }
}
