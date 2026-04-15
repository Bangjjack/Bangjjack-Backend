package com.project.bangjjack.domain.checklist.application;

import com.project.bangjjack.domain.checklist.application.dto.request.RoommatePreferenceRequest;
import com.project.bangjjack.domain.checklist.application.exception.AlreadyPreferenceRegisteredException;
import com.project.bangjjack.domain.checklist.application.exception.DuplicatePreferenceFactorException;
import com.project.bangjjack.domain.checklist.application.usecase.PreferenceUseCase;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreferenceFactor;
import com.project.bangjjack.domain.checklist.domain.service.PreferenceCreateService;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PreferenceUseCaseRegisterTest {

    @Mock
    private UserGetService userGetService;

    @Mock
    private PreferenceCreateService preferenceCreateService;

    @InjectMocks
    private PreferenceUseCase preferenceUseCase;

    private RoommatePreferenceRequest validRequest() {
        return new RoommatePreferenceRequest(
                List.of(
                        RoommatePreferenceFactor.BEDTIME,
                        RoommatePreferenceFactor.NOISE_SENSITIVITY,
                        RoommatePreferenceFactor.SMOKING
                )
        );
    }

    @Nested
    @DisplayName("룸메이트 선호도 등록 시")
    class RegisterPreference {

        @Test
        @DisplayName("유효한 3가지 선호도를 순서대로 등록하면 예외 없이 정상 처리된다")
        void 유효한_요청으로_선호도_등록_성공() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            given(userGetService.getById(userId)).willReturn(user);
            given(preferenceCreateService.createPreference(any(RoommatePreference.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when & then
            assertThatCode(() -> preferenceUseCase.registerPreference(userId, validRequest()))
                    .doesNotThrowAnyException();

            then(preferenceCreateService).should().createPreference(any(RoommatePreference.class));
        }

        @Test
        @DisplayName("선호도 등록 완료 후 User의 isRoommatePreferenceRegistered가 true로 변경된다")
        void 선호도_등록_완료_후_플래그_변경() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            given(userGetService.getById(userId)).willReturn(user);
            given(preferenceCreateService.createPreference(any(RoommatePreference.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            preferenceUseCase.registerPreference(userId, validRequest());

            // then
            assertThat(user.isRoommatePreferenceRegistered()).isTrue();
        }

        @Test
        @DisplayName("이미 선호도를 등록한 사용자가 재요청하면 AlreadyPreferenceRegisteredException이 발생한다")
        void 이미_선호도_등록한_사용자_재요청_시_예외_발생() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            given(userGetService.getById(userId)).willReturn(user);
            given(preferenceCreateService.createPreference(any(RoommatePreference.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            preferenceUseCase.registerPreference(userId, validRequest());

            // when & then
            assertThatThrownBy(() -> preferenceUseCase.registerPreference(userId, validRequest()))
                    .isInstanceOf(AlreadyPreferenceRegisteredException.class);
        }

        @Test
        @DisplayName("중복된 선호도 항목을 포함하면 DuplicatePreferenceFactorException이 발생한다")
        void 중복된_선호도_항목_포함_시_예외_발생() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            RoommatePreferenceRequest duplicateRequest = new RoommatePreferenceRequest(
                    List.of(
                            RoommatePreferenceFactor.BEDTIME,
                            RoommatePreferenceFactor.BEDTIME,
                            RoommatePreferenceFactor.SMOKING
                    )
            );
            given(userGetService.getById(userId)).willReturn(user);

            // when & then
            assertThatThrownBy(() -> preferenceUseCase.registerPreference(userId, duplicateRequest))
                    .isInstanceOf(DuplicatePreferenceFactorException.class);
        }

        @Test
        @DisplayName("요청 순서대로 1~3순위가 저장된다")
        void 요청_순서대로_우선순위_저장() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            RoommatePreferenceRequest request = new RoommatePreferenceRequest(
                    List.of(
                            RoommatePreferenceFactor.CLEANING_HABIT,
                            RoommatePreferenceFactor.WAKE_UP_TIME,
                            RoommatePreferenceFactor.CALL_HABIT
                    )
            );

            given(userGetService.getById(userId)).willReturn(user);
            given(preferenceCreateService.createPreference(any(RoommatePreference.class)))
                    .willAnswer(invocation -> {
                        RoommatePreference saved = invocation.getArgument(0);
                        assertThat(saved.getFirstPriority()).isEqualTo(RoommatePreferenceFactor.CLEANING_HABIT);
                        assertThat(saved.getSecondPriority()).isEqualTo(RoommatePreferenceFactor.WAKE_UP_TIME);
                        assertThat(saved.getThirdPriority()).isEqualTo(RoommatePreferenceFactor.CALL_HABIT);
                        return saved;
                    });

            // when & then
            assertThatCode(() -> preferenceUseCase.registerPreference(userId, request))
                    .doesNotThrowAnyException();
        }
    }
}
