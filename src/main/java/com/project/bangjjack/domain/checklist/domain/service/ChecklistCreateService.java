package com.project.bangjjack.domain.checklist.domain.service;

import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.repository.ChecklistRepository;
import com.project.bangjjack.domain.checklist.domain.repository.LifestyleChecklistSleepHabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistCreateService {

    private final ChecklistRepository checklistRepository;
    private final LifestyleChecklistSleepHabitRepository sleepHabitRepository;

    public LifestyleChecklist createCheckList(LifestyleChecklist checklist) {
        return checklistRepository.save(checklist);
    }

    public void createSleepHabits(List<LifestyleChecklistSleepHabit> sleepHabits) {
        sleepHabitRepository.saveAll(sleepHabits);
    }
}
