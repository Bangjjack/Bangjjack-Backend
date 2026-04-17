package com.project.bangjjack.domain.user.application;

import com.project.bangjjack.domain.department.domain.entity.Department;
import com.project.bangjjack.domain.department.domain.service.DepartmentGetService;
import com.project.bangjjack.domain.user.application.dto.request.UserOnboardingRequest;
import com.project.bangjjack.domain.user.application.exception.AlreadyOnboardedException;
import com.project.bangjjack.domain.user.application.exception.InvalidBirthYearException;
import com.project.bangjjack.domain.user.application.usecase.UserUseCase;
import com.project.bangjjack.domain.user.domain.entity.*;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserUseCaseOnboardingTest {

    @Mock
    private UserGetService userGetService;

    @Mock
    private DepartmentGetService departmentGetService;

    @InjectMocks
    private UserUseCase userUseCase;

    @Nested
    @DisplayName("온보딩 완료 시")
    class CompleteOnboarding {

        @Test
        @DisplayName("유효한 요청으로 온보딩을 완료하면 예외 없이 정상 처리된다")
        void 유효한_요청으로_온보딩_완료_성공() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            Department department = Department.create("소프트웨어학과", Campus.GLOBAL_CAMPUS);

            UserOnboardingRequest request = new UserOnboardingRequest(
                    2000, 2, Gender.MALE,
                    Campus.GLOBAL_CAMPUS, 1L,
                    Semester.SIXTEEN_WEEKS, Dormitory.DORM_1
            );

            given(userGetService.getById(userId)).willReturn(user);
            given(departmentGetService.getById(1L)).willReturn(department);

            // when & then
            assertThatCode(() -> userUseCase.completeOnboarding(userId, request))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("이미 온보딩이 완료된 사용자가 재요청하면 AlreadyOnboardedException이 발생한다")
        void 이미_온보딩_완료된_사용자_재요청_시_예외_발생() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            Department department = Department.create("소프트웨어학과", Campus.GLOBAL_CAMPUS);

            UserOnboardingRequest request = new UserOnboardingRequest(
                    2000, 2, Gender.MALE,
                    Campus.GLOBAL_CAMPUS, 1L,
                    Semester.SIXTEEN_WEEKS, Dormitory.DORM_1
            );

            given(userGetService.getById(userId)).willReturn(user);
            given(departmentGetService.getById(1L)).willReturn(department);

            userUseCase.completeOnboarding(userId, request);

            // when & then
            assertThatThrownBy(() -> userUseCase.completeOnboarding(userId, request))
                    .isInstanceOf(AlreadyOnboardedException.class);
        }

        @Test
        @DisplayName("birthYear가 1899이면 InvalidBirthYearException이 발생한다")
        void birthYear가_1899이면_예외_발생() {
            // given
            Long userId = 1L;
            UserOnboardingRequest request = new UserOnboardingRequest(
                    1899, 2, Gender.MALE,
                    Campus.GLOBAL_CAMPUS, 1L,
                    Semester.SIXTEEN_WEEKS, Dormitory.DORM_1
            );

            // when & then
            assertThatThrownBy(() -> userUseCase.completeOnboarding(userId, request))
                    .isInstanceOf(InvalidBirthYearException.class);
        }

        @Test
        @DisplayName("birthYear가 미래 연도이면 InvalidBirthYearException이 발생한다")
        void birthYear가_미래_연도이면_예외_발생() {
            // given
            Long userId = 1L;
            int futureYear = java.time.LocalDate.now().getYear() + 1;
            UserOnboardingRequest request = new UserOnboardingRequest(
                    futureYear, 2, Gender.MALE,
                    Campus.GLOBAL_CAMPUS, 1L,
                    Semester.SIXTEEN_WEEKS, Dormitory.DORM_1
            );

            // when & then
            assertThatThrownBy(() -> userUseCase.completeOnboarding(userId, request))
                    .isInstanceOf(InvalidBirthYearException.class);
        }

        @Test
        @DisplayName("birthYear가 1900이면 정상적으로 온보딩이 완료된다")
        void birthYear가_1900이면_온보딩_성공() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            Department department = Department.create("의학과", Campus.MEDICAL_CAMPUS);

            UserOnboardingRequest request = new UserOnboardingRequest(
                    1900, 1, Gender.FEMALE,
                    Campus.MEDICAL_CAMPUS, 2L,
                    Semester.TWENTY_FIVE_WEEKS, Dormitory.DORM_2
            );

            given(userGetService.getById(userId)).willReturn(user);
            given(departmentGetService.getById(2L)).willReturn(department);

            // when & then
            assertThatCode(() -> userUseCase.completeOnboarding(userId, request))
                    .doesNotThrowAnyException();
        }
    }
}
