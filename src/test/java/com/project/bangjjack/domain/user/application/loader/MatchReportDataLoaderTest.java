package com.project.bangjjack.domain.user.application.loader;

import com.project.bangjjack.domain.checklist.application.dto.request.LifestyleChecklistRequest;
import com.project.bangjjack.domain.checklist.domain.entity.Bedtime;
import com.project.bangjjack.domain.checklist.domain.entity.CallHabit;
import com.project.bangjjack.domain.checklist.domain.entity.CleaningCycle;
import com.project.bangjjack.domain.checklist.domain.entity.DormStayTime;
import com.project.bangjjack.domain.checklist.domain.entity.IndoorTemperature;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.NoiseSensitivity;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreferenceFactor;
import com.project.bangjjack.domain.checklist.domain.entity.SleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.Smoking;
import com.project.bangjjack.domain.checklist.domain.entity.WakeUpTime;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistGetService;
import com.project.bangjjack.domain.checklist.domain.service.RoommatePreferenceGetService;
import com.project.bangjjack.domain.post.application.exception.ChecklistNotRegisteredException;
import com.project.bangjjack.domain.post.application.exception.PreferenceNotRegisteredException;
import com.project.bangjjack.domain.post.application.exception.SelfMatchNotAllowedException;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisCommand;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchReportDataLoader")
class MatchReportDataLoaderTest {

    @Mock
    private UserGetService userGetService;
    @Mock
    private ChecklistGetService checklistGetService;
    @Mock
    private RoommatePreferenceGetService roommatePreferenceGetService;

    @InjectMocks
    private MatchReportDataLoader matchReportDataLoader;

    private User userWithId(Long id) throws Exception {
        User user = User.create("provider-" + id, "테스트유저" + id, "test" + id + "@gachon.ac.kr", null);
        user.completeOnboarding(2000, 2, Gender.MALE, Campus.GLOBAL_CAMPUS, null, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
        Field idField = user.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, id);
        return user;
    }

    private LifestyleChecklist checklistFor(User user) {
        return LifestyleChecklist.create(user, new LifestyleChecklistRequest(
                Bedtime.BETWEEN_22_24,
                WakeUpTime.BETWEEN_6_8,
                List.of(SleepHabit.NONE),
                CleaningCycle.ONCE_OR_TWICE_A_WEEK,
                DormStayTime.HALF_AND_HALF,
                CallHabit.WHISPER,
                IndoorTemperature.INSENSITIVE,
                NoiseSensitivity.NORMAL,
                Smoking.NON_SMOKER
        ));
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
    @DisplayName("loadCommand")
    class LoadCommand {

        @Test
        @DisplayName("양측 체크리스트·선호도 등록 완료된 경우 MatchAnalysisCommand를 반환한다")
        void 정상_조회_시_커맨드_반환() throws Exception {
            // given
            Long requesterId = 1L;
            Long targetUserId = 2L;
            User requester = userWithId(requesterId);
            User target = userWithId(targetUserId);
            LifestyleChecklist requesterChecklist = checklistFor(requester);
            LifestyleChecklist targetChecklist = checklistFor(target);

            given(userGetService.getById(requesterId)).willReturn(requester);
            given(userGetService.getById(targetUserId)).willReturn(target);
            given(checklistGetService.findByUser(requester)).willReturn(Optional.of(requesterChecklist));
            given(checklistGetService.findByUser(target)).willReturn(Optional.of(targetChecklist));
            given(checklistGetService.findSleepHabitsByChecklist(requesterChecklist)).willReturn(List.of());
            given(checklistGetService.findSleepHabitsByChecklist(targetChecklist)).willReturn(List.of());
            given(roommatePreferenceGetService.findByUser(requester)).willReturn(Optional.of(preferenceFor(requester)));
            given(roommatePreferenceGetService.findByUser(target)).willReturn(Optional.of(preferenceFor(target)));

            // when
            MatchAnalysisCommand command = matchReportDataLoader.loadCommand(requesterId, targetUserId);

            // then
            assertThat(command.requester().user().getId()).isEqualTo(requesterId);
            assertThat(command.author().user().getId()).isEqualTo(targetUserId);
            assertThat(command.requester().checklist()).isSameAs(requesterChecklist);
            assertThat(command.author().checklist()).isSameAs(targetChecklist);
        }

        @Test
        @DisplayName("requesterId와 targetUserId가 동일하면 SelfMatchNotAllowedException이 발생한다")
        void 본인_요청_시_예외_발생() {
            // given
            Long userId = 1L;

            // when & then
            assertThatThrownBy(() -> matchReportDataLoader.loadCommand(userId, userId))
                    .isInstanceOf(SelfMatchNotAllowedException.class);
        }

        @Test
        @DisplayName("존재하지 않는 targetUserId면 UserNotFoundException이 발생한다")
        void 존재하지_않는_targetUserId_시_예외_발생() throws Exception {
            // given
            Long requesterId = 1L;
            Long targetUserId = 999L;
            User requester = userWithId(requesterId);

            given(userGetService.getById(requesterId)).willReturn(requester);
            given(userGetService.getById(targetUserId)).willThrow(UserNotFoundException.class);

            // when & then
            assertThatThrownBy(() -> matchReportDataLoader.loadCommand(requesterId, targetUserId))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("요청자 체크리스트 미등록이면 ChecklistNotRegisteredException이 발생한다")
        void 요청자_체크리스트_미등록_시_예외() throws Exception {
            // given
            Long requesterId = 1L;
            Long targetUserId = 2L;
            User requester = userWithId(requesterId);
            User target = userWithId(targetUserId);

            given(userGetService.getById(requesterId)).willReturn(requester);
            given(userGetService.getById(targetUserId)).willReturn(target);
            given(checklistGetService.findByUser(requester)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> matchReportDataLoader.loadCommand(requesterId, targetUserId))
                    .isInstanceOf(ChecklistNotRegisteredException.class);
        }

        @Test
        @DisplayName("대상 사용자 체크리스트 미등록이면 ChecklistNotRegisteredException이 발생한다")
        void 대상_체크리스트_미등록_시_예외() throws Exception {
            // given
            Long requesterId = 1L;
            Long targetUserId = 2L;
            User requester = userWithId(requesterId);
            User target = userWithId(targetUserId);
            LifestyleChecklist requesterChecklist = checklistFor(requester);

            given(userGetService.getById(requesterId)).willReturn(requester);
            given(userGetService.getById(targetUserId)).willReturn(target);
            given(checklistGetService.findByUser(requester)).willReturn(Optional.of(requesterChecklist));
            given(checklistGetService.findSleepHabitsByChecklist(requesterChecklist)).willReturn(List.of());
            given(roommatePreferenceGetService.findByUser(requester)).willReturn(Optional.of(preferenceFor(requester)));
            given(checklistGetService.findByUser(target)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> matchReportDataLoader.loadCommand(requesterId, targetUserId))
                    .isInstanceOf(ChecklistNotRegisteredException.class);
        }

        @Test
        @DisplayName("요청자 선호도 미등록이면 PreferenceNotRegisteredException이 발생한다")
        void 요청자_선호도_미등록_시_예외() throws Exception {
            // given
            Long requesterId = 1L;
            Long targetUserId = 2L;
            User requester = userWithId(requesterId);
            User target = userWithId(targetUserId);
            LifestyleChecklist requesterChecklist = checklistFor(requester);

            given(userGetService.getById(requesterId)).willReturn(requester);
            given(userGetService.getById(targetUserId)).willReturn(target);
            given(checklistGetService.findByUser(requester)).willReturn(Optional.of(requesterChecklist));
            given(checklistGetService.findSleepHabitsByChecklist(requesterChecklist)).willReturn(List.of());
            given(roommatePreferenceGetService.findByUser(requester)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> matchReportDataLoader.loadCommand(requesterId, targetUserId))
                    .isInstanceOf(PreferenceNotRegisteredException.class);
        }

        @Test
        @DisplayName("대상 사용자 선호도 미등록이면 PreferenceNotRegisteredException이 발생한다")
        void 대상_선호도_미등록_시_예외() throws Exception {
            // given
            Long requesterId = 1L;
            Long targetUserId = 2L;
            User requester = userWithId(requesterId);
            User target = userWithId(targetUserId);
            LifestyleChecklist requesterChecklist = checklistFor(requester);
            LifestyleChecklist targetChecklist = checklistFor(target);

            given(userGetService.getById(requesterId)).willReturn(requester);
            given(userGetService.getById(targetUserId)).willReturn(target);
            given(checklistGetService.findByUser(requester)).willReturn(Optional.of(requesterChecklist));
            given(checklistGetService.findSleepHabitsByChecklist(requesterChecklist)).willReturn(List.of());
            given(roommatePreferenceGetService.findByUser(requester)).willReturn(Optional.of(preferenceFor(requester)));
            given(checklistGetService.findByUser(target)).willReturn(Optional.of(targetChecklist));
            given(checklistGetService.findSleepHabitsByChecklist(targetChecklist)).willReturn(List.of());
            given(roommatePreferenceGetService.findByUser(target)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> matchReportDataLoader.loadCommand(requesterId, targetUserId))
                    .isInstanceOf(PreferenceNotRegisteredException.class);
        }
    }
}
