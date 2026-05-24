package com.project.bangjjack.domain.checklist.domain.service;

import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.repository.LifestyleChecklistSleepHabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistUpdateService {

    private final LifestyleChecklistSleepHabitRepository sleepHabitRepository;

    public void softDeleteSleepHabits(List<LifestyleChecklistSleepHabit> sleepHabits) {
        sleepHabits.forEach(LifestyleChecklistSleepHabit::softDelete);
        sleepHabitRepository.saveAll(sleepHabits);
    }
}
