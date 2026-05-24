package com.project.bangjjack.domain.checklist.domain.service;

import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.SleepHabit;
import com.project.bangjjack.domain.checklist.domain.repository.LifestyleChecklistSleepHabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistUpdateService {

    private final LifestyleChecklistSleepHabitRepository sleepHabitRepository;

    public void replaceSleepHabits(LifestyleChecklist checklist, List<SleepHabit> newHabitTypes) {
        List<LifestyleChecklistSleepHabit> existingHabits = sleepHabitRepository.findByChecklistAndDeletedFalse(checklist);
        existingHabits.forEach(LifestyleChecklistSleepHabit::softDelete);
        sleepHabitRepository.saveAll(existingHabits);

        List<LifestyleChecklistSleepHabit> newHabits = newHabitTypes.stream()
                .map(habit -> LifestyleChecklistSleepHabit.create(checklist, habit))
                .toList();
        sleepHabitRepository.saveAll(newHabits);
    }
}
