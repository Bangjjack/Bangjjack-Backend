package com.project.bangjjack.domain.checklist.domain.service;

import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.repository.ChecklistRepository;
import com.project.bangjjack.domain.checklist.domain.repository.LifestyleChecklistSleepHabitRepository;
import com.project.bangjjack.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChecklistGetService {

    private final ChecklistRepository checklistRepository;
    private final LifestyleChecklistSleepHabitRepository sleepHabitRepository;

    public Optional<LifestyleChecklist> findByUser(User user) {
        return checklistRepository.findByUserAndDeletedFalse(user);
    }

    public List<LifestyleChecklistSleepHabit> findSleepHabitsByChecklist(LifestyleChecklist checklist) {
        return sleepHabitRepository.findByChecklistAndDeletedFalse(checklist);
    }
}
