package com.project.bangjjack.domain.user.application.usecase;

import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistBundle;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistGetService;
import com.project.bangjjack.domain.checklist.domain.service.RoommatePreferenceGetService;
import com.project.bangjjack.domain.department.domain.entity.Department;
import com.project.bangjjack.domain.department.domain.service.DepartmentGetService;
import com.project.bangjjack.domain.user.application.dto.request.UserOnboardingRequest;
import com.project.bangjjack.domain.user.application.dto.response.MyProfileResponse;
import com.project.bangjjack.domain.user.application.dto.response.UserBasicTagResponse;
import com.project.bangjjack.domain.user.application.exception.AlreadyOnboardedException;
import com.project.bangjjack.domain.user.application.exception.InvalidBirthYearException;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserUseCase {

    private final UserGetService userGetService;
    private final DepartmentGetService departmentGetService;
    private final ChecklistGetService checklistGetService;
    private final RoommatePreferenceGetService roommatePreferenceGetService;

    @Transactional
    public void completeOnboarding(Long userId, UserOnboardingRequest request) {
        validateBirthYear(request.birthYear());

        User user = userGetService.getById(userId);
        if (user.isOnboarded()) {
            throw new AlreadyOnboardedException();
        }

        Department department = departmentGetService.getById(request.departmentId());

        user.completeOnboarding(
                request.birthYear(),
                request.grade(),
                request.gender(),
                request.campus(),
                department,
                request.semester(),
                request.dormitory()
        );
    }

    public UserBasicTagResponse getUserBasicTag(Long userId) {
        User user = userGetService.getById(userId);
        RoommatePreference preference = roommatePreferenceGetService.getByUser(user);
        return UserBasicTagResponse.of(user, preference);
    }

    public MyProfileResponse getMyProfile(Long userId) {
        User user = userGetService.getById(userId);
        ChecklistBundle checklistBundle = checklistGetService.findByUser(user)
                .map(this::toChecklistBundle)
                .orElse(null);
        RoommatePreference preference = roommatePreferenceGetService.findByUser(user)
                .orElse(null);
        return MyProfileResponse.of(user, checklistBundle, preference);
    }

    private ChecklistBundle toChecklistBundle(LifestyleChecklist checklist) {
        List<LifestyleChecklistSleepHabit> sleepHabits =
                checklistGetService.findSleepHabitsByChecklist(checklist);
        return new ChecklistBundle(checklist, sleepHabits);
    }

    private void validateBirthYear(int birthYear) {
        int currentYear = LocalDate.now().getYear();
        if (birthYear < 1900 || birthYear > currentYear) {
            throw new InvalidBirthYearException();
        }
    }
}
