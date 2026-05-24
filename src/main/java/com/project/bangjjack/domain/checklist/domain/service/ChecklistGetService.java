package com.project.bangjjack.domain.checklist.domain.service;

import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.repository.ChecklistRepository;
import com.project.bangjjack.domain.checklist.domain.repository.LifestyleChecklistSleepHabitRepository;
import com.project.bangjjack.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Map<Long, ChecklistBundle> getChecklistBundlesByUserIds(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Set<Long> lookupIds = new HashSet<>(userIds);
        List<LifestyleChecklist> checklists = checklistRepository.findAllByUserIdInAndDeletedFalse(lookupIds);
        if (checklists.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> checklistIds = checklists.stream().map(LifestyleChecklist::getId).toList();
        Map<Long, List<LifestyleChecklistSleepHabit>> sleepHabitsByChecklistId = sleepHabitRepository
                .findByChecklistIdInAndDeletedFalse(checklistIds).stream()
                .collect(Collectors.groupingBy(habit -> habit.getChecklist().getId()));

        return checklists.stream()
                .collect(Collectors.toMap(
                        c -> c.getUser().getId(),
                        c -> new ChecklistBundle(c, sleepHabitsByChecklistId.getOrDefault(c.getId(), List.of()))
                ));
    }
}
