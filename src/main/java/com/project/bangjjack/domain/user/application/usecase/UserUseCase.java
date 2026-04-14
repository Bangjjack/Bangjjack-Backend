package com.project.bangjjack.domain.user.application.usecase;

import com.project.bangjjack.domain.department.domain.entity.Department;
import com.project.bangjjack.domain.department.domain.service.DepartmentGetService;
import com.project.bangjjack.domain.user.application.dto.request.UserOnboardingRequest;
import com.project.bangjjack.domain.user.application.exception.AlreadyOnboardedException;
import com.project.bangjjack.domain.user.application.exception.InvalidBirthYearException;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserUseCase {

    private final UserGetService userGetService;
    private final DepartmentGetService departmentGetService;

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

    private void validateBirthYear(int birthYear) {
        int currentYear = LocalDate.now().getYear();
        if (birthYear < 1900 || birthYear > currentYear) {
            throw new InvalidBirthYearException();
        }
    }
}
