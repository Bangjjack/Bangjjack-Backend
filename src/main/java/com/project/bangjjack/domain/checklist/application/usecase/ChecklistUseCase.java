package com.project.bangjjack.domain.checklist.application.usecase;

import com.project.bangjjack.domain.checklist.application.dto.request.LifestyleChecklistRequest;
import com.project.bangjjack.domain.checklist.application.dto.request.UpdateLifestyleChecklistRequest;
import com.project.bangjjack.domain.checklist.application.dto.response.MyLifestyleChecklistResponse;
import com.project.bangjjack.domain.checklist.application.exception.AlreadyChecklistRegisteredException;
import com.project.bangjjack.domain.checklist.application.exception.ChecklistNotFoundException;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistBundle;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistCreateService;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistGetService;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistUpdateService;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChecklistUseCase {

    private final UserGetService userGetService;
    private final ChecklistCreateService checklistCreateService;
    private final ChecklistGetService checklistGetService;
    private final ChecklistUpdateService checklistUpdateService;

    @Transactional
    public void registerChecklist(Long userId, LifestyleChecklistRequest request) {
        User user = userGetService.getById(userId);

        if (user.isChecklistRegistered()) {
            throw new AlreadyChecklistRegisteredException();
        }

        LifestyleChecklist checklist = checklistCreateService.createCheckList(LifestyleChecklist.create(user, request));

        List<LifestyleChecklistSleepHabit> sleepHabits = request.sleepHabits().stream()
                .map(habit -> LifestyleChecklistSleepHabit.create(checklist, habit))
                .toList();
        checklistCreateService.createSleepHabits(sleepHabits);

        user.completeChecklistRegistration();
    }

    public MyLifestyleChecklistResponse getMyChecklist(Long userId) {
        User user = userGetService.getById(userId);
        LifestyleChecklist checklist = checklistGetService.findByUser(user)
                .orElseThrow(ChecklistNotFoundException::new);
        List<LifestyleChecklistSleepHabit> sleepHabits = checklistGetService.findSleepHabitsByChecklist(checklist);

        return MyLifestyleChecklistResponse.from(new ChecklistBundle(checklist, sleepHabits));
    }

    @Transactional
    public void updateMyChecklist(Long userId, UpdateLifestyleChecklistRequest request) {
        User user = userGetService.getById(userId);
        LifestyleChecklist checklist = checklistGetService.findByUser(user)
                .orElseThrow(ChecklistNotFoundException::new);

        checklist.update(
                request.bedtime(),
                request.wakeUpTime(),
                request.cleaningCycle(),
                request.dormStayTime(),
                request.callHabit(),
                request.indoorTemperature(),
                request.noiseSensitivity(),
                request.smoking()
        );

        checklistUpdateService.replaceSleepHabits(checklist, request.sleepHabits());
    }
}
