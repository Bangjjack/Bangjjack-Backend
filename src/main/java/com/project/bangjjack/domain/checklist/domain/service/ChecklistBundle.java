package com.project.bangjjack.domain.checklist.domain.service;

import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;

import java.util.List;

public record ChecklistBundle(
        LifestyleChecklist checklist,
        List<LifestyleChecklistSleepHabit> sleepHabits
) {
}
