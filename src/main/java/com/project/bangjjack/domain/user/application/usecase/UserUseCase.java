package com.project.bangjjack.domain.user.application.usecase;

import com.project.bangjjack.domain.checklist.application.dto.response.LifestyleChecklistResponse;
import com.project.bangjjack.domain.checklist.application.exception.DuplicatePreferenceFactorException;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreferenceFactor;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistBundle;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistGetService;
import com.project.bangjjack.domain.checklist.domain.service.PreferenceCreateService;
import com.project.bangjjack.domain.checklist.domain.service.RoommatePreferenceGetService;
import com.project.bangjjack.domain.department.domain.entity.Department;
import com.project.bangjjack.domain.department.domain.service.DepartmentGetService;
import com.project.bangjjack.domain.user.application.dto.request.UpdateMyProfileRequest;
import com.project.bangjjack.domain.user.application.dto.request.UserOnboardingRequest;
import com.project.bangjjack.domain.user.application.dto.response.MyProfileResponse;
import com.project.bangjjack.domain.user.application.dto.response.UserBasicTagResponse;
import com.project.bangjjack.domain.user.application.dto.response.UserProfileResponse;
import com.project.bangjjack.domain.user.application.exception.AlreadyOnboardedException;
import com.project.bangjjack.domain.user.application.exception.InvalidBirthYearException;
import com.project.bangjjack.domain.user.application.exception.NotOnboardedException;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserUseCase {

    private final UserGetService userGetService;
    private final DepartmentGetService departmentGetService;
    private final ChecklistGetService checklistGetService;
    private final RoommatePreferenceGetService roommatePreferenceGetService;
    private final PreferenceCreateService preferenceCreateService;

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

    @Transactional
    public void updateMyProfile(Long userId, UpdateMyProfileRequest request) {
        validateBirthYear(request.birthYear());

        List<RoommatePreferenceFactor> preferences = request.preferences();
        if (new HashSet<>(preferences).size() != preferences.size()) {
            throw new DuplicatePreferenceFactorException();
        }

        User user = userGetService.getById(userId);
        if (!user.isOnboarded()) {
            throw new NotOnboardedException();
        }

        Department department = departmentGetService.getById(request.departmentId());

        user.editProfile(
                request.birthYear(),
                request.grade(),
                request.gender(),
                request.campus(),
                department,
                request.semester(),
                request.dormitory()
        );

        Optional<RoommatePreference> existing = roommatePreferenceGetService.findByUser(user);
        if (existing.isPresent()) {
            existing.get().update(preferences.get(0), preferences.get(1), preferences.get(2));
        } else {
            RoommatePreference preference = RoommatePreference.create(
                    user,
                    preferences.get(0),
                    preferences.get(1),
                    preferences.get(2)
            );
            preferenceCreateService.createPreference(preference);
            user.completeRoommatePreferenceRegistration();
        }
    }

    public UserBasicTagResponse getUserBasicTag(Long userId) {
        User user = userGetService.getById(userId);
        RoommatePreference preference = roommatePreferenceGetService.getByUser(user);
        return UserBasicTagResponse.of(user, preference);
    }

    public MyProfileResponse getMyProfile(Long userId) {
        User user = userGetService.getById(userId);
        Department department = user.getDepartment();
        ChecklistBundle checklistBundle = checklistGetService.findByUser(user)
                .map(this::toChecklistBundle)
                .orElse(null);
        RoommatePreference preference = roommatePreferenceGetService.findByUser(user)
                .orElse(null);
        return MyProfileResponse.of(user, department, checklistBundle, preference);
    }

    public UserProfileResponse getUserProfile(Long viewerId, Long userId) {
        User targetUser = userGetService.getById(userId);
        Department department = targetUser.getDepartment();

        Map<Long, ChecklistBundle> bundles =
                checklistGetService.getChecklistBundlesByUserIds(List.of(viewerId, userId));
        ChecklistBundle viewerBundle = bundles.get(viewerId);
        ChecklistBundle targetBundle = bundles.get(userId);
        LifestyleChecklistResponse lifestyleChecklist = targetBundle == null
                ? null
                : LifestyleChecklistResponse.from(targetBundle, viewerBundle);

        RoommatePreference preference = roommatePreferenceGetService.findByUser(targetUser)
                .orElse(null);

        return UserProfileResponse.of(targetUser, department, preference, lifestyleChecklist);
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
