package com.project.bangjjack.domain.user.application.usecase;

import com.project.bangjjack.domain.checklist.application.dto.request.LifestyleChecklistRequest;
import com.project.bangjjack.domain.checklist.application.exception.DuplicatePreferenceFactorException;
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
import com.project.bangjjack.domain.checklist.domain.service.ChecklistBundle;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistGetService;
import com.project.bangjjack.domain.checklist.domain.service.PreferenceCreateService;
import com.project.bangjjack.domain.checklist.domain.service.RoommatePreferenceGetService;
import com.project.bangjjack.domain.department.domain.entity.Department;
import com.project.bangjjack.domain.department.domain.service.DepartmentGetService;
import com.project.bangjjack.domain.user.application.dto.request.UpdateMyProfileRequest;
import com.project.bangjjack.domain.user.application.dto.response.UserProfileResponse;
import com.project.bangjjack.domain.user.application.exception.InvalidBirthYearException;
import com.project.bangjjack.domain.user.application.exception.NotOnboardedException;
import com.project.bangjjack.domain.user.application.exception.UserNotFoundException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserUseCase")
class UserUseCaseTest {

    @Mock
    private UserGetService userGetService;
    @Mock
    private DepartmentGetService departmentGetService;
    @Mock
    private ChecklistGetService checklistGetService;
    @Mock
    private RoommatePreferenceGetService roommatePreferenceGetService;
    @Mock
    private PreferenceCreateService preferenceCreateService;

    @InjectMocks
    private UserUseCase userUseCase;

    private Department departmentWithId(Long id, String name) throws Exception {
        Department department = Department.create(name, Campus.GLOBAL_CAMPUS);
        Field idField = department.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(department, id);
        return department;
    }

    private User onboardedUserWithId(Long id, Department department) throws Exception {
        User user = User.create("provider-" + id, "유저" + id, "u" + id + "@gachon.ac.kr",
                "https://img.example.com/" + id);
        user.completeOnboarding(2000 + id.intValue(), 2, Gender.FEMALE, Campus.GLOBAL_CAMPUS,
                department, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
        Field idField = user.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, id);
        return user;
    }

    private User unonboardedUserWithId(Long id) throws Exception {
        User user = User.create("provider-" + id, "유저" + id, "u" + id + "@gachon.ac.kr", null);
        Field idField = user.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, id);
        return user;
    }

    private LifestyleChecklist checklistFor(User user,
                                            Bedtime bedtime,
                                            WakeUpTime wakeUpTime,
                                            Smoking smoking) {
        return LifestyleChecklist.create(user, new LifestyleChecklistRequest(
                bedtime,
                wakeUpTime,
                List.of(SleepHabit.NONE),
                CleaningCycle.ONCE_OR_TWICE_A_WEEK,
                DormStayTime.HALF_AND_HALF,
                CallHabit.WHISPER,
                IndoorTemperature.INSENSITIVE,
                NoiseSensitivity.NORMAL,
                smoking
        ));
    }

    private List<LifestyleChecklistSleepHabit> sleepHabitsOf(LifestyleChecklist checklist, SleepHabit... habits) {
        return List.of(habits).stream()
                .map(h -> LifestyleChecklistSleepHabit.create(checklist, h))
                .toList();
    }

    private RoommatePreference preferenceFor(User user) {
        return RoommatePreference.create(
                user,
                RoommatePreferenceFactor.BEDTIME,
                RoommatePreferenceFactor.CLEANING_HABIT,
                RoommatePreferenceFactor.NOISE_SENSITIVITY
        );
    }

    @Nested
    @DisplayName("getUserProfile")
    class GetUserProfile {

        @Test
        @DisplayName("viewer/target 모두 등록 완료 시 일치 항목은 matched=true, 다른 항목은 false 로 반환된다")
        void viewer와_target_모두_등록_완료() throws Exception {
            // given
            Long viewerId = 1L;
            Long targetId = 2L;
            Department viewerDept = departmentWithId(100L, "소프트웨어학과");
            Department targetDept = departmentWithId(101L, "디자인학과");
            User viewer = onboardedUserWithId(viewerId, viewerDept);
            User target = onboardedUserWithId(targetId, targetDept);

            LifestyleChecklist viewerChecklist = checklistFor(viewer,
                    Bedtime.BETWEEN_22_24, WakeUpTime.BETWEEN_6_8, Smoking.NON_SMOKER);
            LifestyleChecklist targetChecklist = checklistFor(target,
                    Bedtime.BETWEEN_22_24, WakeUpTime.AFTER_10, Smoking.CIGARETTE);

            ChecklistBundle viewerBundle = new ChecklistBundle(
                    viewerChecklist, sleepHabitsOf(viewerChecklist, SleepHabit.NONE));
            ChecklistBundle targetBundle = new ChecklistBundle(
                    targetChecklist, sleepHabitsOf(targetChecklist, SleepHabit.NONE));

            given(userGetService.getById(targetId)).willReturn(target);
            given(checklistGetService.getChecklistBundlesByUserIds(List.of(viewerId, targetId)))
                    .willReturn(Map.of(viewerId, viewerBundle, targetId, targetBundle));
            given(roommatePreferenceGetService.findByUser(target))
                    .willReturn(Optional.of(preferenceFor(target)));

            // when
            UserProfileResponse result = userUseCase.getUserProfile(viewerId, targetId);

            // then
            assertThat(result.userId()).isEqualTo(targetId);
            assertThat(result.username()).isEqualTo("유저2");
            assertThat(result.profileImage()).isEqualTo("https://img.example.com/2");
            assertThat(result.grade()).isEqualTo(2);
            assertThat(result.birthYear()).isEqualTo(2002);
            assertThat(result.departmentName()).isEqualTo("디자인학과");
            assertThat(result.semester()).isEqualTo(Semester.SIXTEEN_WEEKS);
            assertThat(result.dormitory()).isEqualTo(Dormitory.DORM_1);
            assertThat(result.roommatePreferences()).containsExactly(
                    RoommatePreferenceFactor.BEDTIME,
                    RoommatePreferenceFactor.CLEANING_HABIT,
                    RoommatePreferenceFactor.NOISE_SENSITIVITY
            );

            assertThat(result.lifestyleChecklist()).isNotNull();
            assertThat(result.lifestyleChecklist().bedtime().value()).isEqualTo(Bedtime.BETWEEN_22_24);
            assertThat(result.lifestyleChecklist().bedtime().matched()).isTrue();
            assertThat(result.lifestyleChecklist().wakeUpTime().value()).isEqualTo(WakeUpTime.AFTER_10);
            assertThat(result.lifestyleChecklist().wakeUpTime().matched()).isFalse();
            assertThat(result.lifestyleChecklist().smoking().value()).isEqualTo(Smoking.CIGARETTE);
            assertThat(result.lifestyleChecklist().smoking().matched()).isFalse();
            assertThat(result.lifestyleChecklist().sleepHabits().value()).containsExactly(SleepHabit.NONE);
            assertThat(result.lifestyleChecklist().sleepHabits().matched()).isTrue();
        }

        @Test
        @DisplayName("viewer 체크리스트 미등록 시 모든 항목 matched=null 로 반환된다")
        void viewer_체크리스트_미등록() throws Exception {
            // given
            Long viewerId = 1L;
            Long targetId = 2L;
            Department targetDept = departmentWithId(101L, "디자인학과");
            User target = onboardedUserWithId(targetId, targetDept);

            LifestyleChecklist targetChecklist = checklistFor(target,
                    Bedtime.AFTER_2, WakeUpTime.AFTER_10, Smoking.CIGARETTE);
            ChecklistBundle targetBundle = new ChecklistBundle(
                    targetChecklist, sleepHabitsOf(targetChecklist, SleepHabit.SNORING));

            given(userGetService.getById(targetId)).willReturn(target);
            given(checklistGetService.getChecklistBundlesByUserIds(List.of(viewerId, targetId)))
                    .willReturn(Map.of(targetId, targetBundle));
            given(roommatePreferenceGetService.findByUser(target))
                    .willReturn(Optional.of(preferenceFor(target)));

            // when
            UserProfileResponse result = userUseCase.getUserProfile(viewerId, targetId);

            // then
            assertThat(result.lifestyleChecklist()).isNotNull();
            assertThat(result.lifestyleChecklist().bedtime().matched()).isNull();
            assertThat(result.lifestyleChecklist().wakeUpTime().matched()).isNull();
            assertThat(result.lifestyleChecklist().sleepHabits().matched()).isNull();
            assertThat(result.lifestyleChecklist().smoking().matched()).isNull();
            assertThat(result.lifestyleChecklist().bedtime().value()).isEqualTo(Bedtime.AFTER_2);
            assertThat(result.lifestyleChecklist().smoking().value()).isEqualTo(Smoking.CIGARETTE);
            assertThat(result.lifestyleChecklist().sleepHabits().value()).containsExactly(SleepHabit.SNORING);
        }

        @Test
        @DisplayName("target 체크리스트 미등록 시 lifestyleChecklist=null 로 반환된다 (예외 없음)")
        void target_체크리스트_미등록() throws Exception {
            // given
            Long viewerId = 1L;
            Long targetId = 2L;
            Department targetDept = departmentWithId(101L, "디자인학과");
            User target = onboardedUserWithId(targetId, targetDept);

            given(userGetService.getById(targetId)).willReturn(target);
            given(checklistGetService.getChecklistBundlesByUserIds(List.of(viewerId, targetId)))
                    .willReturn(Map.of());
            given(roommatePreferenceGetService.findByUser(target))
                    .willReturn(Optional.of(preferenceFor(target)));

            // when
            UserProfileResponse result = userUseCase.getUserProfile(viewerId, targetId);

            // then
            assertThat(result.lifestyleChecklist()).isNull();
            assertThat(result.roommatePreferences()).hasSize(3);
        }

        @Test
        @DisplayName("target 선호도 미등록 시 roommatePreferences=null 로 반환된다 (예외 없음)")
        void target_선호도_미등록() throws Exception {
            // given
            Long viewerId = 1L;
            Long targetId = 2L;
            Department targetDept = departmentWithId(101L, "디자인학과");
            User target = onboardedUserWithId(targetId, targetDept);

            given(userGetService.getById(targetId)).willReturn(target);
            given(checklistGetService.getChecklistBundlesByUserIds(List.of(viewerId, targetId)))
                    .willReturn(Map.of());
            given(roommatePreferenceGetService.findByUser(target))
                    .willReturn(Optional.empty());

            // when
            UserProfileResponse result = userUseCase.getUserProfile(viewerId, targetId);

            // then
            assertThat(result.roommatePreferences()).isNull();
            assertThat(result.lifestyleChecklist()).isNull();
        }

        @Test
        @DisplayName("target 온보딩 미완료 시 학적/기숙사 필드는 null 로 반환되고 200 OK 동작한다")
        void target_온보딩_미완료() throws Exception {
            // given
            Long viewerId = 1L;
            Long targetId = 2L;
            User target = unonboardedUserWithId(targetId);

            given(userGetService.getById(targetId)).willReturn(target);
            given(checklistGetService.getChecklistBundlesByUserIds(List.of(viewerId, targetId)))
                    .willReturn(Map.of());
            given(roommatePreferenceGetService.findByUser(target))
                    .willReturn(Optional.empty());

            // when
            UserProfileResponse result = userUseCase.getUserProfile(viewerId, targetId);

            // then
            assertThat(result.userId()).isEqualTo(targetId);
            assertThat(result.username()).isEqualTo("유저2");
            assertThat(result.grade()).isNull();
            assertThat(result.birthYear()).isNull();
            assertThat(result.departmentName()).isNull();
            assertThat(result.semester()).isNull();
            assertThat(result.dormitory()).isNull();
            assertThat(result.roommatePreferences()).isNull();
            assertThat(result.lifestyleChecklist()).isNull();
        }

        @Test
        @DisplayName("자기 자신 조회 시 모든 체크리스트 항목이 matched=true 로 반환된다")
        void 자기_자신_조회() throws Exception {
            // given
            Long userId = 1L;
            Department dept = departmentWithId(100L, "소프트웨어학과");
            User user = onboardedUserWithId(userId, dept);

            LifestyleChecklist checklist = checklistFor(user,
                    Bedtime.BETWEEN_22_24, WakeUpTime.BETWEEN_6_8, Smoking.NON_SMOKER);
            ChecklistBundle bundle = new ChecklistBundle(
                    checklist, sleepHabitsOf(checklist, SleepHabit.NONE));

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistGetService.getChecklistBundlesByUserIds(List.of(userId, userId)))
                    .willReturn(Map.of(userId, bundle));
            given(roommatePreferenceGetService.findByUser(user))
                    .willReturn(Optional.of(preferenceFor(user)));

            // when
            UserProfileResponse result = userUseCase.getUserProfile(userId, userId);

            // then
            assertThat(result.lifestyleChecklist()).isNotNull();
            assertThat(result.lifestyleChecklist().bedtime().matched()).isTrue();
            assertThat(result.lifestyleChecklist().wakeUpTime().matched()).isTrue();
            assertThat(result.lifestyleChecklist().sleepHabits().matched()).isTrue();
            assertThat(result.lifestyleChecklist().cleaningCycle().matched()).isTrue();
            assertThat(result.lifestyleChecklist().dormStayTime().matched()).isTrue();
            assertThat(result.lifestyleChecklist().callHabit().matched()).isTrue();
            assertThat(result.lifestyleChecklist().indoorTemperature().matched()).isTrue();
            assertThat(result.lifestyleChecklist().noiseSensitivity().matched()).isTrue();
            assertThat(result.lifestyleChecklist().smoking().matched()).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 userId 조회 시 UserNotFoundException 이 전파된다")
        void 존재하지_않는_userId() {
            // given
            Long viewerId = 1L;
            Long targetId = 999L;
            given(userGetService.getById(targetId)).willThrow(new UserNotFoundException());

            // when & then
            assertThatThrownBy(() -> userUseCase.getUserProfile(viewerId, targetId))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateMyProfile")
    class UpdateMyProfile {

        private UpdateMyProfileRequest validRequest(Long departmentId, List<RoommatePreferenceFactor> preferences) {
            return new UpdateMyProfileRequest(
                    1999,
                    3,
                    Gender.MALE,
                    Campus.MEDICAL_CAMPUS,
                    departmentId,
                    Semester.TWENTY_FIVE_WEEKS,
                    Dormitory.DORM_2,
                    preferences
            );
        }

        @Test
        @DisplayName("선호도 등록된 사용자가 호출 시 User 7개 필드와 RoommatePreference 3순위가 모두 갱신된다")
        void 선호도_등록_사용자_정상_수정() throws Exception {
            // given
            Long userId = 1L;
            Long deptId = 200L;
            Department originalDept = departmentWithId(100L, "소프트웨어학과");
            User user = onboardedUserWithId(userId, originalDept);
            Department newDept = departmentWithId(deptId, "디자인학과");
            RoommatePreference existing = preferenceFor(user);

            UpdateMyProfileRequest request = validRequest(deptId, List.of(
                    RoommatePreferenceFactor.SMOKING,
                    RoommatePreferenceFactor.WAKE_UP_TIME,
                    RoommatePreferenceFactor.ITEM_SHARING
            ));

            given(userGetService.getById(userId)).willReturn(user);
            given(departmentGetService.getById(deptId)).willReturn(newDept);
            given(roommatePreferenceGetService.findByUser(user)).willReturn(Optional.of(existing));

            // when
            userUseCase.updateMyProfile(userId, request);

            // then
            assertThat(user.getBirthYear()).isEqualTo(1999);
            assertThat(user.getGrade()).isEqualTo(3);
            assertThat(user.getGender()).isEqualTo(Gender.MALE);
            assertThat(user.getCampus()).isEqualTo(Campus.MEDICAL_CAMPUS);
            assertThat(user.getDepartment()).isSameAs(newDept);
            assertThat(user.getSemester()).isEqualTo(Semester.TWENTY_FIVE_WEEKS);
            assertThat(user.getDormitory()).isEqualTo(Dormitory.DORM_2);
            assertThat(user.isOnboarded()).isTrue();
            assertThat(existing.getFirstPriority()).isEqualTo(RoommatePreferenceFactor.SMOKING);
            assertThat(existing.getSecondPriority()).isEqualTo(RoommatePreferenceFactor.WAKE_UP_TIME);
            assertThat(existing.getThirdPriority()).isEqualTo(RoommatePreferenceFactor.ITEM_SHARING);
        }

        @Test
        @DisplayName("선호도 미등록 사용자가 호출 시 RoommatePreference 신규 생성 및 isRoommatePreferenceRegistered=true 로 동기화된다")
        void 선호도_미등록_사용자_신규_생성() throws Exception {
            // given
            Long userId = 1L;
            Long deptId = 200L;
            Department originalDept = departmentWithId(100L, "소프트웨어학과");
            User user = onboardedUserWithId(userId, originalDept);
            Department newDept = departmentWithId(deptId, "디자인학과");

            UpdateMyProfileRequest request = validRequest(deptId, List.of(
                    RoommatePreferenceFactor.BEDTIME,
                    RoommatePreferenceFactor.CLEANING_HABIT,
                    RoommatePreferenceFactor.NOISE_SENSITIVITY
            ));

            given(userGetService.getById(userId)).willReturn(user);
            given(departmentGetService.getById(deptId)).willReturn(newDept);
            given(roommatePreferenceGetService.findByUser(user)).willReturn(Optional.empty());

            // when
            userUseCase.updateMyProfile(userId, request);

            // then
            ArgumentCaptor<RoommatePreference> captor = ArgumentCaptor.forClass(RoommatePreference.class);
            then(preferenceCreateService).should().createPreference(captor.capture());
            RoommatePreference created = captor.getValue();
            assertThat(created.getUser()).isSameAs(user);
            assertThat(created.getFirstPriority()).isEqualTo(RoommatePreferenceFactor.BEDTIME);
            assertThat(created.getSecondPriority()).isEqualTo(RoommatePreferenceFactor.CLEANING_HABIT);
            assertThat(created.getThirdPriority()).isEqualTo(RoommatePreferenceFactor.NOISE_SENSITIVITY);
            assertThat(user.isRoommatePreferenceRegistered()).isTrue();
        }

        @Test
        @DisplayName("isOnboarded=false 사용자가 호출 시 NotOnboardedException 이 발생한다")
        void 온보딩_미완료_사용자_차단() throws Exception {
            // given
            Long userId = 1L;
            User user = unonboardedUserWithId(userId);
            UpdateMyProfileRequest request = validRequest(200L, List.of(
                    RoommatePreferenceFactor.BEDTIME,
                    RoommatePreferenceFactor.CLEANING_HABIT,
                    RoommatePreferenceFactor.NOISE_SENSITIVITY
            ));

            given(userGetService.getById(userId)).willReturn(user);

            // when & then
            assertThatThrownBy(() -> userUseCase.updateMyProfile(userId, request))
                    .isInstanceOf(NotOnboardedException.class);
        }

        @Test
        @DisplayName("birthYear 가 1900 미만이면 InvalidBirthYearException 이 발생한다")
        void birthYear_하한_위반() {
            // given
            Long userId = 1L;
            UpdateMyProfileRequest request = new UpdateMyProfileRequest(
                    1899, 3, Gender.MALE, Campus.MEDICAL_CAMPUS, 200L,
                    Semester.TWENTY_FIVE_WEEKS, Dormitory.DORM_2,
                    List.of(RoommatePreferenceFactor.BEDTIME,
                            RoommatePreferenceFactor.CLEANING_HABIT,
                            RoommatePreferenceFactor.NOISE_SENSITIVITY)
            );

            // when & then
            assertThatThrownBy(() -> userUseCase.updateMyProfile(userId, request))
                    .isInstanceOf(InvalidBirthYearException.class);
        }

        @Test
        @DisplayName("birthYear 가 현재 연도 초과이면 InvalidBirthYearException 이 발생한다")
        void birthYear_상한_위반() {
            // given
            Long userId = 1L;
            int futureYear = java.time.LocalDate.now().getYear() + 1;
            UpdateMyProfileRequest request = new UpdateMyProfileRequest(
                    futureYear, 3, Gender.MALE, Campus.MEDICAL_CAMPUS, 200L,
                    Semester.TWENTY_FIVE_WEEKS, Dormitory.DORM_2,
                    List.of(RoommatePreferenceFactor.BEDTIME,
                            RoommatePreferenceFactor.CLEANING_HABIT,
                            RoommatePreferenceFactor.NOISE_SENSITIVITY)
            );

            // when & then
            assertThatThrownBy(() -> userUseCase.updateMyProfile(userId, request))
                    .isInstanceOf(InvalidBirthYearException.class);
        }

        @Test
        @DisplayName("preferences 에 중복 항목이 포함되면 DuplicatePreferenceFactorException 이 발생한다")
        void preferences_중복() {
            // given
            Long userId = 1L;
            UpdateMyProfileRequest request = validRequest(200L, List.of(
                    RoommatePreferenceFactor.BEDTIME,
                    RoommatePreferenceFactor.BEDTIME,
                    RoommatePreferenceFactor.SMOKING
            ));

            // when & then
            assertThatThrownBy(() -> userUseCase.updateMyProfile(userId, request))
                    .isInstanceOf(DuplicatePreferenceFactorException.class);
        }

        @Test
        @DisplayName("존재하지 않는 userId 호출 시 UserNotFoundException 이 전파된다")
        void 존재하지_않는_userId() {
            // given
            Long userId = 999L;
            UpdateMyProfileRequest request = validRequest(200L, List.of(
                    RoommatePreferenceFactor.BEDTIME,
                    RoommatePreferenceFactor.CLEANING_HABIT,
                    RoommatePreferenceFactor.NOISE_SENSITIVITY
            ));

            given(userGetService.getById(userId)).willThrow(new UserNotFoundException());

            // when & then
            assertThatThrownBy(() -> userUseCase.updateMyProfile(userId, request))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}
