package com.project.bangjjack.domain.checklist.application;

import com.project.bangjjack.domain.checklist.application.dto.request.LifestyleChecklistRequest;
import com.project.bangjjack.domain.checklist.application.dto.response.MyLifestyleChecklistResponse;
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
import com.project.bangjjack.domain.checklist.domain.service.ChecklistGetService;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ChecklistUseCaseGetMyTest {

    @Mock
    private UserGetService userGetService;

    @Mock
    private ChecklistGetService checklistGetService;

    @InjectMocks
    private ChecklistUseCase checklistUseCase;

    private LifestyleChecklistRequest validRequest(List<SleepHabit> sleepHabits) {
        return new LifestyleChecklistRequest(
                Bedtime.BETWEEN_22_24,
                WakeUpTime.BETWEEN_6_8,
                sleepHabits,
                CleaningCycle.ONCE_OR_TWICE_A_WEEK,
                DormStayTime.MOSTLY_INSIDE,
                CallHabit.OUTSIDE_ONLY,
                IndoorTemperature.SENSITIVE_TO_COLD,
                NoiseSensitivity.SLIGHTLY_SENSITIVE,
                Smoking.NON_SMOKER
        );
    }

    @Nested
    @DisplayName("본인 생활습관 체크리스트 조회 시")
    class GetMyChecklist {

        @Test
        @DisplayName("체크리스트가 등록된 사용자가 조회하면 9개 항목이 모두 채워진 응답을 반환한다")
        void 등록된_사용자_정상_조회() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            LifestyleChecklist checklist = LifestyleChecklist.create(user, validRequest(List.of(SleepHabit.SNORING)));
            List<LifestyleChecklistSleepHabit> sleepHabits = List.of(
                    LifestyleChecklistSleepHabit.create(checklist, SleepHabit.SNORING)
            );

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistGetService.findByUser(user)).willReturn(Optional.of(checklist));
            given(checklistGetService.findSleepHabitsByChecklist(checklist)).willReturn(sleepHabits);

            // when
            MyLifestyleChecklistResponse response = checklistUseCase.getMyChecklist(userId);

            // then
            assertThat(response.bedtime()).isEqualTo(Bedtime.BETWEEN_22_24);
            assertThat(response.wakeUpTime()).isEqualTo(WakeUpTime.BETWEEN_6_8);
            assertThat(response.sleepHabits()).containsExactly(SleepHabit.SNORING);
            assertThat(response.cleaningCycle()).isEqualTo(CleaningCycle.ONCE_OR_TWICE_A_WEEK);
            assertThat(response.dormStayTime()).isEqualTo(DormStayTime.MOSTLY_INSIDE);
            assertThat(response.callHabit()).isEqualTo(CallHabit.OUTSIDE_ONLY);
            assertThat(response.indoorTemperature()).isEqualTo(IndoorTemperature.SENSITIVE_TO_COLD);
            assertThat(response.noiseSensitivity()).isEqualTo(NoiseSensitivity.SLIGHTLY_SENSITIVE);
            assertThat(response.smoking()).isEqualTo(Smoking.NON_SMOKER);
        }

        @Test
        @DisplayName("잠버릇이 복수로 등록되어 있으면 응답의 sleepHabits에 모두 포함된다")
        void 잠버릇_복수_조회() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            LifestyleChecklist checklist = LifestyleChecklist.create(
                    user,
                    validRequest(List.of(SleepHabit.TOSS_AND_TURN, SleepHabit.SNORING, SleepHabit.TEETH_GRINDING))
            );
            List<LifestyleChecklistSleepHabit> sleepHabits = List.of(
                    LifestyleChecklistSleepHabit.create(checklist, SleepHabit.TOSS_AND_TURN),
                    LifestyleChecklistSleepHabit.create(checklist, SleepHabit.SNORING),
                    LifestyleChecklistSleepHabit.create(checklist, SleepHabit.TEETH_GRINDING)
            );

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistGetService.findByUser(user)).willReturn(Optional.of(checklist));
            given(checklistGetService.findSleepHabitsByChecklist(checklist)).willReturn(sleepHabits);

            // when
            MyLifestyleChecklistResponse response = checklistUseCase.getMyChecklist(userId);

            // then
            assertThat(response.sleepHabits())
                    .containsExactly(SleepHabit.TOSS_AND_TURN, SleepHabit.SNORING, SleepHabit.TEETH_GRINDING);
        }

        @Test
        @DisplayName("잠버릇이 NONE 단일로 등록되어 있어도 List로 정상 응답된다")
        void 잠버릇_NONE_단일_조회() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            LifestyleChecklist checklist = LifestyleChecklist.create(user, validRequest(List.of(SleepHabit.NONE)));
            List<LifestyleChecklistSleepHabit> sleepHabits = List.of(
                    LifestyleChecklistSleepHabit.create(checklist, SleepHabit.NONE)
            );

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistGetService.findByUser(user)).willReturn(Optional.of(checklist));
            given(checklistGetService.findSleepHabitsByChecklist(checklist)).willReturn(sleepHabits);

            // when
            MyLifestyleChecklistResponse response = checklistUseCase.getMyChecklist(userId);

            // then
            assertThat(response.sleepHabits()).containsExactly(SleepHabit.NONE);
        }

        @Test
        @DisplayName("체크리스트를 등록하지 않은 사용자가 조회하면 ChecklistNotFoundException이 발생한다")
        void 미등록_사용자_조회_시_예외_발생() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistGetService.findByUser(user)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> checklistUseCase.getMyChecklist(userId))
                    .isInstanceOf(ChecklistNotFoundException.class);
        }
    }
}
