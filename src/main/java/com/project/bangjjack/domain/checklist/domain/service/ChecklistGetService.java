package com.project.bangjjack.domain.checklist.domain.service;

import com.project.bangjjack.domain.checklist.application.dto.response.LifestyleChecklistResponse;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.repository.ChecklistRepository;
import com.project.bangjjack.domain.checklist.domain.repository.LifestyleChecklistSleepHabitRepository;
import com.project.bangjjack.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public Map<Long, LifestyleChecklistResponse> getChecklistResponsesByUserIds(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<LifestyleChecklist> checklists = checklistRepository.findAllByUserIdInAndDeletedFalse(userIds);
        if (checklists.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> checklistIds = checklists.stream().map(LifestyleChecklist::getId).toList();
        Map<Long, List<LifestyleChecklistSleepHabit>> sleepHabitsByChecklistId = sleepHabitRepository
                .findByChecklistIdInAndDeletedFalse(checklistIds).stream()
                .collect(Collectors.groupingBy(habit -> habit.getChecklist().getId()));

        return checklists.stream().collect(Collectors.toMap(
                checklist -> checklist.getUser().getId(),
                checklist -> LifestyleChecklistResponse.from(
                        checklist,
                        sleepHabitsByChecklistId.getOrDefault(checklist.getId(), List.of())
                )
        ));
    }
}
