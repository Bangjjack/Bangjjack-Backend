package com.project.bangjjack.domain.user.application;

import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreferenceFactor;
import com.project.bangjjack.domain.checklist.domain.service.RoommatePreferenceGetService;
import com.project.bangjjack.domain.department.domain.service.DepartmentGetService;
import com.project.bangjjack.domain.user.application.dto.response.UserBasicTagResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserUseCaseGetBasicTagTest {

    @Mock
    private UserGetService userGetService;

    @Mock
    private DepartmentGetService departmentGetService;

    @Mock
    private RoommatePreferenceGetService roommatePreferenceGetService;

    @InjectMocks
    private UserUseCase userUseCase;

    @Nested
    @DisplayName("기본 태그 조회 시")
    class GetUserBasicTag {

        @Test
        @DisplayName("유효한 userId로 조회하면 semester, dormitory, roommatePreferences를 반환한다")
        void 유효한_userId로_기본_태그_조회_성공() {
            // given
            Long userId = 1L;
            User user = User.create("provider-1", "테스트유저", "test@gachon.ac.kr", null);
            user.completeOnboarding(2000, 2, Gender.MALE, Campus.GLOBAL_CAMPUS, null,
                    Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);

            RoommatePreference preference = RoommatePreference.create(user,
                    RoommatePreferenceFactor.BEDTIME,
                    RoommatePreferenceFactor.CLEANING_HABIT,
                    RoommatePreferenceFactor.NOISE_SENSITIVITY);

            given(userGetService.getById(userId)).willReturn(user);
            given(roommatePreferenceGetService.getByUser(user)).willReturn(preference);

            // when
            UserBasicTagResponse response = userUseCase.getUserBasicTag(userId);

            // then
            assertThat(response.semester()).isEqualTo(Semester.SIXTEEN_WEEKS);
            assertThat(response.dormitory()).isEqualTo(Dormitory.DORM_1);
            assertThat(response.roommatePreferences()).containsExactly(
                    RoommatePreferenceFactor.BEDTIME,
                    RoommatePreferenceFactor.CLEANING_HABIT,
                    RoommatePreferenceFactor.NOISE_SENSITIVITY
            );
        }

        @Test
        @DisplayName("존재하지 않는 userId로 조회하면 UserNotFoundException이 발생한다")
        void 존재하지_않는_userId로_조회_시_예외_발생() {
            // given
            Long nonExistentUserId = 999L;
            given(userGetService.getById(nonExistentUserId)).willThrow(UserNotFoundException.class);

            // when & then
            assertThatThrownBy(() -> userUseCase.getUserBasicTag(nonExistentUserId))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}
