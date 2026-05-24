package com.project.bangjjack.domain.checklist.application;

import com.project.bangjjack.domain.checklist.application.dto.request.LifestyleChecklistRequest;
import com.project.bangjjack.domain.checklist.application.dto.request.UpdateLifestyleChecklistRequest;
import com.project.bangjjack.domain.checklist.application.exception.ChecklistNotFoundException;
import com.project.bangjjack.domain.checklist.application.usecase.ChecklistUseCase;
import com.project.bangjjack.domain.checklist.domain.entity.Bedtime;
import com.project.bangjjack.domain.checklist.domain.entity.CallHabit;
import com.project.bangjjack.domain.checklist.domain.entity.CleaningCycle;
import com.project.bangjjack.domain.checklist.domain.entity.DormStayTime;
import com.project.bangjjack.domain.checklist.domain.entity.IndoorTemperature;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.NoiseSensitivity;
import com.project.bangjjack.domain.checklist.domain.entity.SleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.Smoking;
import com.project.bangjjack.domain.checklist.domain.entity.WakeUpTime;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistCreateService;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistGetService;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistUpdateService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChecklistUseCaseUpdateTest {

    @Mock
    private UserGetService userGetService;

    @Mock
    private ChecklistGetService checklistGetService;

    @Mock
    private ChecklistUpdateService checklistUpdateService;

    @Mock
    private ChecklistCreateService checklistCreateService;

    @InjectMocks
    private ChecklistUseCase checklistUseCase;

    private LifestyleChecklistRequest initialRequest() {
        return new LifestyleChecklistRequest(
                Bedtime.BETWEEN_22_24,
                WakeUpTime.BETWEEN_6_8,
                List.of(SleepHabit.SNORING),
                CleaningCycle.ONCE_OR_TWICE_A_WEEK,
                DormStayTime.MOSTLY_INSIDE,
                CallHabit.OUTSIDE_ONLY,
                IndoorTemperature.SENSITIVE_TO_COLD,
                NoiseSensitivity.SLIGHTLY_SENSITIVE,
                Smoking.NON_SMOKER
        );
    }

    private UpdateLifestyleChecklistRequest updateRequest(List<SleepHabit> sleepHabits) {
        return new UpdateLifestyleChecklistRequest(
                Bedtime.AFTER_2,
                WakeUpTime.AFTER_10,
                sleepHabits,
                CleaningCycle.RARELY,
                DormStayTime.MOSTLY_OUTSIDE,
                CallHabit.INSIDE_OK,
                IndoorTemperature.SENSITIVE_TO_HEAT,
                NoiseSensitivity.VERY_INSENSITIVE,
                Smoking.CIGARETTE
        );
    }

    @Nested
    @DisplayName("본인 생활습관 체크리스트 수정 시")
    class UpdateMyChecklist {

        @Test
        @DisplayName("유효한 요청으로 수정하면 8개 필드가 모두 새 값으로 갱신된다")
        void 유효한_요청으로_수정_성공() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            LifestyleChecklist checklist = LifestyleChecklist.create(user, initialRequest());

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistGetService.findByUser(user)).willReturn(Optional.of(checklist));
            given(checklistGetService.findSleepHabitsByChecklist(checklist)).willReturn(new ArrayList<>());

            // when
            checklistUseCase.updateMyChecklist(userId, updateRequest(List.of(SleepHabit.TOSS_AND_TURN)));

            // then
            assertThat(checklist.getBedtime()).isEqualTo(Bedtime.AFTER_2);
            assertThat(checklist.getWakeUpTime()).isEqualTo(WakeUpTime.AFTER_10);
            assertThat(checklist.getCleaningCycle()).isEqualTo(CleaningCycle.RARELY);
            assertThat(checklist.getDormStayTime()).isEqualTo(DormStayTime.MOSTLY_OUTSIDE);
            assertThat(checklist.getCallHabit()).isEqualTo(CallHabit.INSIDE_OK);
            assertThat(checklist.getIndoorTemperature()).isEqualTo(IndoorTemperature.SENSITIVE_TO_HEAT);
            assertThat(checklist.getNoiseSensitivity()).isEqualTo(NoiseSensitivity.VERY_INSENSITIVE);
            assertThat(checklist.getSmoking()).isEqualTo(Smoking.CIGARETTE);
        }

        @Test
        @DisplayName("수정 시 기존 잠버릇은 모두 소프트 삭제되고 신규 잠버릇이 일괄 삽입된다")
        void 잠버릇_교체_검증() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            LifestyleChecklist checklist = LifestyleChecklist.create(user, initialRequest());
            List<LifestyleChecklistSleepHabit> existingHabits = new ArrayList<>(List.of(
                    LifestyleChecklistSleepHabit.create(checklist, SleepHabit.SNORING)
            ));

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistGetService.findByUser(user)).willReturn(Optional.of(checklist));
            given(checklistGetService.findSleepHabitsByChecklist(checklist)).willReturn(existingHabits);

            // when
            checklistUseCase.updateMyChecklist(
                    userId,
                    updateRequest(List.of(SleepHabit.TOSS_AND_TURN, SleepHabit.TEETH_GRINDING))
            );

            // then
            then(checklistUpdateService).should().softDeleteSleepHabits(existingHabits);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<LifestyleChecklistSleepHabit>> captor = ArgumentCaptor.forClass(List.class);
            then(checklistCreateService).should().createSleepHabits(captor.capture());
            List<LifestyleChecklistSleepHabit> saved = captor.getValue();
            assertThat(saved).hasSize(2);
            assertThat(saved.stream().map(LifestyleChecklistSleepHabit::getSleepHabit).toList())
                    .containsExactly(SleepHabit.TOSS_AND_TURN, SleepHabit.TEETH_GRINDING);
        }

        @Test
        @DisplayName("수정 후에도 User.isChecklistRegistered는 true로 유지된다")
        void 플래그_유지_검증() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            user.completeChecklistRegistration();
            LifestyleChecklist checklist = LifestyleChecklist.create(user, initialRequest());

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistGetService.findByUser(user)).willReturn(Optional.of(checklist));
            given(checklistGetService.findSleepHabitsByChecklist(checklist)).willReturn(new ArrayList<>());

            // when
            checklistUseCase.updateMyChecklist(userId, updateRequest(List.of(SleepHabit.NONE)));

            // then
            assertThat(user.isChecklistRegistered()).isTrue();
        }

        @Test
        @DisplayName("체크리스트를 등록하지 않은 사용자가 수정 요청 시 ChecklistNotFoundException이 발생한다")
        void 미등록_사용자_수정_시_예외_발생() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistGetService.findByUser(user)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> checklistUseCase.updateMyChecklist(userId, updateRequest(List.of(SleepHabit.SNORING))))
                    .isInstanceOf(ChecklistNotFoundException.class);

            then(checklistUpdateService).shouldHaveNoInteractions();
            then(checklistCreateService).should(org.mockito.Mockito.never()).createSleepHabits(anyList());
        }
    }
}
