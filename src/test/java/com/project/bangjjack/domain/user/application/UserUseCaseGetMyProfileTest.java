package com.project.bangjjack.domain.user.application;

import com.project.bangjjack.domain.checklist.application.dto.request.LifestyleChecklistRequest;
import com.project.bangjjack.domain.checklist.domain.entity.Bedtime;
import com.project.bangjjack.domain.checklist.domain.entity.CallHabit;
import com.project.bangjjack.domain.checklist.domain.entity.CleaningCycle;
import com.project.bangjjack.domain.checklist.domain.entity.DormStayTime;
import com.project.bangjjack.domain.checklist.domain.entity.IndoorTemperature;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.NoiseSensitivity;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreferenceFactor;
import com.project.bangjjack.domain.checklist.domain.entity.SleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.Smoking;
import com.project.bangjjack.domain.checklist.domain.entity.WakeUpTime;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistGetService;
import com.project.bangjjack.domain.checklist.domain.service.RoommatePreferenceGetService;
import com.project.bangjjack.domain.department.domain.entity.Department;
import com.project.bangjjack.domain.department.domain.service.DepartmentGetService;
import com.project.bangjjack.domain.user.application.dto.response.MyProfileResponse;
import com.project.bangjjack.domain.user.application.exception.UserNotFoundException;
import com.project.bangjjack.domain.user.application.usecase.UserUseCase;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.Gender;
import com.project.bangjjack.domain.user.domain.entity.Semester;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserUseCaseGetMyProfileTest {

    @Mock
    private UserGetService userGetService;

    @Mock
    private DepartmentGetService departmentGetService;

    @Mock
    private ChecklistGetService checklistGetService;

    @Mock
    private RoommatePreferenceGetService roommatePreferenceGetService;

    @InjectMocks
    private UserUseCase userUseCase;

    @Nested
    @DisplayName("내 프로필 조회 시")
    class GetMyProfile {

        @Test
        @DisplayName("온보딩/체크리스트/선호도 모두 완료한 사용자는 모든 필드가 채워진 응답을 받는다")
        void 모든_정보_등록_사용자_프로필_조회_성공() {
            // given
            Long userId = 1L;
            Department department = Department.create("소프트웨어학과", Campus.GLOBAL_CAMPUS);
            ReflectionTestUtils.setField(department, "id", 10L);

            User user = User.create("provider-1", "테스트유저", "test@gachon.ac.kr", "image-url");
            user.completeOnboarding(2000, 2, Gender.MALE, Campus.GLOBAL_CAMPUS, department,
                    Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);

            LifestyleChecklist checklist = LifestyleChecklist.create(user, sampleChecklistRequest());
            LifestyleChecklistSleepHabit habit1 = LifestyleChecklistSleepHabit.create(checklist, SleepHabit.TOSS_AND_TURN);
            LifestyleChecklistSleepHabit habit2 = LifestyleChecklistSleepHabit.create(checklist, SleepHabit.SNORING);

            RoommatePreference preference = RoommatePreference.create(user,
                    RoommatePreferenceFactor.BEDTIME,
                    RoommatePreferenceFactor.CLEANING_HABIT,
                    RoommatePreferenceFactor.NOISE_SENSITIVITY);

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistGetService.findByUser(user)).willReturn(Optional.of(checklist));
            given(checklistGetService.findSleepHabitsByChecklist(checklist)).willReturn(List.of(habit1, habit2));
            given(roommatePreferenceGetService.findByUser(user)).willReturn(Optional.of(preference));

            // when
            MyProfileResponse response = userUseCase.getMyProfile(userId);

            // then
            assertThat(response.username()).isEqualTo("테스트유저");
            assertThat(response.email()).isEqualTo("test@gachon.ac.kr");
            assertThat(response.profileImage()).isEqualTo("image-url");
            assertThat(response.birthYear()).isEqualTo(2000);
            assertThat(response.gender()).isEqualTo(Gender.MALE);
            assertThat(response.campus()).isEqualTo(Campus.GLOBAL_CAMPUS);
            assertThat(response.departmentId()).isEqualTo(10L);
            assertThat(response.departmentName()).isEqualTo("소프트웨어학과");
            assertThat(response.grade()).isEqualTo(2);
            assertThat(response.semester()).isEqualTo(Semester.SIXTEEN_WEEKS);
            assertThat(response.dormitory()).isEqualTo(Dormitory.DORM_1);
            assertThat(response.checklist()).isNotNull();
            assertThat(response.checklist().bedtime()).isEqualTo(Bedtime.BEFORE_22);
            assertThat(response.checklist().sleepHabits())
                    .containsExactly(SleepHabit.TOSS_AND_TURN, SleepHabit.SNORING);
            assertThat(response.roommatePreferences()).containsExactly(
                    RoommatePreferenceFactor.BEDTIME,
                    RoommatePreferenceFactor.CLEANING_HABIT,
                    RoommatePreferenceFactor.NOISE_SENSITIVITY
            );
        }

        @Test
        @DisplayName("온보딩 미완료 사용자는 인적사항/학적 필드가 모두 null인 응답을 받는다")
        void 온보딩_미완료_사용자_프로필_조회() {
            // given
            Long userId = 2L;
            User user = User.create("provider-2", "신규유저", "new@gachon.ac.kr", null);

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistGetService.findByUser(user)).willReturn(Optional.empty());
            given(roommatePreferenceGetService.findByUser(user)).willReturn(Optional.empty());

            // when
            MyProfileResponse response = userUseCase.getMyProfile(userId);

            // then
            assertThat(response.username()).isEqualTo("신규유저");
            assertThat(response.email()).isEqualTo("new@gachon.ac.kr");
            assertThat(response.profileImage()).isNull();
            assertThat(response.birthYear()).isNull();
            assertThat(response.gender()).isNull();
            assertThat(response.campus()).isNull();
            assertThat(response.departmentId()).isNull();
            assertThat(response.departmentName()).isNull();
            assertThat(response.grade()).isNull();
            assertThat(response.semester()).isNull();
            assertThat(response.dormitory()).isNull();
            assertThat(response.checklist()).isNull();
            assertThat(response.roommatePreferences()).isNull();
        }

        @Test
        @DisplayName("체크리스트 미등록 사용자는 checklist=null로 200 OK 응답을 받는다")
        void 체크리스트_미등록_사용자_조회() {
            // given
            Long userId = 3L;
            User user = onboardedUserWithoutDepartment();
            RoommatePreference preference = RoommatePreference.create(user,
                    RoommatePreferenceFactor.SMOKING,
                    RoommatePreferenceFactor.BEDTIME,
                    RoommatePreferenceFactor.CALL_HABIT);

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistGetService.findByUser(user)).willReturn(Optional.empty());
            given(roommatePreferenceGetService.findByUser(user)).willReturn(Optional.of(preference));

            // when
            MyProfileResponse response = userUseCase.getMyProfile(userId);

            // then
            assertThat(response.checklist()).isNull();
            assertThat(response.roommatePreferences()).containsExactly(
                    RoommatePreferenceFactor.SMOKING,
                    RoommatePreferenceFactor.BEDTIME,
                    RoommatePreferenceFactor.CALL_HABIT
            );
        }

        @Test
        @DisplayName("선호도 미등록 사용자는 roommatePreferences=null로 200 OK 응답을 받는다")
        void 선호도_미등록_사용자_조회() {
            // given
            Long userId = 4L;
            User user = onboardedUserWithoutDepartment();
            LifestyleChecklist checklist = LifestyleChecklist.create(user, sampleChecklistRequest());

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistGetService.findByUser(user)).willReturn(Optional.of(checklist));
            given(checklistGetService.findSleepHabitsByChecklist(checklist)).willReturn(List.of());
            given(roommatePreferenceGetService.findByUser(user)).willReturn(Optional.empty());

            // when
            MyProfileResponse response = userUseCase.getMyProfile(userId);

            // then
            assertThat(response.checklist()).isNotNull();
            assertThat(response.checklist().sleepHabits()).isEmpty();
            assertThat(response.roommatePreferences()).isNull();
        }

        @Test
        @DisplayName("체크리스트와 선호도 모두 미등록 사용자는 두 필드 모두 null인 응답을 받는다")
        void 체크리스트_선호도_모두_미등록_사용자_조회() {
            // given
            Long userId = 5L;
            User user = onboardedUserWithoutDepartment();

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistGetService.findByUser(user)).willReturn(Optional.empty());
            given(roommatePreferenceGetService.findByUser(user)).willReturn(Optional.empty());

            // when
            MyProfileResponse response = userUseCase.getMyProfile(userId);

            // then
            assertThat(response.checklist()).isNull();
            assertThat(response.roommatePreferences()).isNull();
        }

        @Test
        @DisplayName("존재하지 않는 userId로 조회하면 UserNotFoundException이 발생한다")
        void 존재하지_않는_userId로_조회_시_예외_발생() {
            // given
            Long nonExistentUserId = 999L;
            given(userGetService.getById(nonExistentUserId)).willThrow(UserNotFoundException.class);

            // when & then
            assertThatThrownBy(() -> userUseCase.getMyProfile(nonExistentUserId))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    private User onboardedUserWithoutDepartment() {
        User user = User.create("provider-x", "온보딩유저", "onboarded@gachon.ac.kr", null);
        user.completeOnboarding(1999, 3, Gender.FEMALE, Campus.MEDICAL_CAMPUS, null,
                Semester.TWENTY_FIVE_WEEKS, Dormitory.DORM_2);
        return user;
    }

    private LifestyleChecklistRequest sampleChecklistRequest() {
        return new LifestyleChecklistRequest(
                Bedtime.BEFORE_22,
                WakeUpTime.BEFORE_6,
                List.of(SleepHabit.TOSS_AND_TURN),
                CleaningCycle.ALMOST_DAILY,
                DormStayTime.MOSTLY_INSIDE,
                CallHabit.INSIDE_OK,
                IndoorTemperature.INSENSITIVE,
                NoiseSensitivity.NORMAL,
                Smoking.NON_SMOKER
        );
    }
}
