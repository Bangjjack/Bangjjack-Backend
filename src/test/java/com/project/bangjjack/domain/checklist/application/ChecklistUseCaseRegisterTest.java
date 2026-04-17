package com.project.bangjjack.domain.checklist.application;

import com.project.bangjjack.domain.checklist.application.dto.request.LifestyleChecklistRequest;
import com.project.bangjjack.domain.checklist.application.exception.AlreadyChecklistRegisteredException;
import com.project.bangjjack.domain.checklist.application.usecase.ChecklistUseCase;
import com.project.bangjjack.domain.checklist.domain.entity.*;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistCreateService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ChecklistUseCaseRegisterTest {

    @Mock
    private UserGetService userGetService;

    @Mock
    private ChecklistCreateService checklistCreateService;

    @InjectMocks
    private ChecklistUseCase checklistUseCase;

    private LifestyleChecklistRequest validRequest() {
        return new LifestyleChecklistRequest(
                Bedtime.BETWEEN_22_24,
                WakeUpTime.BETWEEN_6_8,
                List.of(SleepHabit.TOSS_AND_TURN, SleepHabit.SNORING),
                CleaningCycle.ONCE_OR_TWICE_A_WEEK,
                DormStayTime.MOSTLY_INSIDE,
                CallHabit.OUTSIDE_ONLY,
                IndoorTemperature.SENSITIVE_TO_COLD,
                NoiseSensitivity.SLIGHTLY_SENSITIVE,
                Smoking.NON_SMOKER
        );
    }

    @Nested
    @DisplayName("생활습관 체크리스트 등록 시")
    class RegisterChecklist {

        @Test
        @DisplayName("유효한 요청으로 체크리스트를 등록하면 예외 없이 정상 처리된다")
        void 유효한_요청으로_체크리스트_등록_성공() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            LifestyleChecklist savedChecklist = LifestyleChecklist.create(user, validRequest());

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistCreateService.createCheckList(any(LifestyleChecklist.class))).willReturn(savedChecklist);

            // when & then
            assertThatCode(() -> checklistUseCase.registerChecklist(userId, validRequest()))
                    .doesNotThrowAnyException();

            then(checklistCreateService).should().createCheckList(any(LifestyleChecklist.class));
            then(checklistCreateService).should().createSleepHabits(anyList());
        }

        @Test
        @DisplayName("체크리스트 등록 완료 후 User의 isChecklistRegistered가 true로 변경된다")
        void 체크리스트_등록_완료_후_플래그_변경() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            LifestyleChecklist savedChecklist = LifestyleChecklist.create(user, validRequest());

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistCreateService.createCheckList(any(LifestyleChecklist.class))).willReturn(savedChecklist);

            // when
            checklistUseCase.registerChecklist(userId, validRequest());

            // then
            assertThat(user.isChecklistRegistered()).isTrue();
        }

        @Test
        @DisplayName("잠버릇을 복수 선택하여 등록하면 선택한 수만큼 saveAllSleepHabits가 호출된다")
        void 잠버릇_복수_선택_등록_성공() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            LifestyleChecklistRequest request = new LifestyleChecklistRequest(
                    Bedtime.AFTER_2,
                    WakeUpTime.AFTER_10,
                    List.of(SleepHabit.SNORING, SleepHabit.TEETH_GRINDING, SleepHabit.FREQUENT_WAKING),
                    CleaningCycle.RARELY,
                    DormStayTime.MOSTLY_OUTSIDE,
                    CallHabit.INSIDE_OK,
                    IndoorTemperature.SENSITIVE_TO_HEAT,
                    NoiseSensitivity.VERY_INSENSITIVE,
                    Smoking.CIGARETTE
            );
            LifestyleChecklist savedChecklist = LifestyleChecklist.create(user, request);

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistCreateService.createCheckList(any(LifestyleChecklist.class))).willReturn(savedChecklist);

            // when & then
            assertThatCode(() -> checklistUseCase.registerChecklist(userId, request))
                    .doesNotThrowAnyException();

            then(checklistCreateService).should().createSleepHabits(anyList());
        }

        @Test
        @DisplayName("이미 체크리스트를 등록한 사용자가 재요청하면 AlreadyChecklistRegisteredException이 발생한다")
        void 이미_체크리스트_등록한_사용자_재요청_시_예외_발생() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            LifestyleChecklist savedChecklist = LifestyleChecklist.create(user, validRequest());

            given(userGetService.getById(userId)).willReturn(user);
            given(checklistCreateService.createCheckList(any(LifestyleChecklist.class))).willReturn(savedChecklist);

            checklistUseCase.registerChecklist(userId, validRequest());

            // when & then
            assertThatThrownBy(() -> checklistUseCase.registerChecklist(userId, validRequest()))
                    .isInstanceOf(AlreadyChecklistRegisteredException.class);
        }
    }
}
