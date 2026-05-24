package com.project.bangjjack.domain.checklist.domain.repository;

import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface LifestyleChecklistSleepHabitRepository extends JpaRepository<LifestyleChecklistSleepHabit, Long> {

    List<LifestyleChecklistSleepHabit> findByChecklistAndDeletedFalse(LifestyleChecklist checklist);

    List<LifestyleChecklistSleepHabit> findByChecklistIdInAndDeletedFalse(Collection<Long> checklistIds);
}
