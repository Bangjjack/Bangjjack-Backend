package com.project.bangjjack.domain.post.application.usecase;

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
import com.project.bangjjack.domain.post.application.dto.response.MatchRateResponse;
import com.project.bangjjack.domain.post.application.exception.AiServiceUnavailableException;
import com.project.bangjjack.domain.post.application.exception.ChecklistNotRegisteredException;
import com.project.bangjjack.domain.post.application.exception.PostNotFoundException;
import com.project.bangjjack.domain.post.application.exception.PreferenceNotRegisteredException;
import com.project.bangjjack.domain.post.application.exception.SelfMatchNotAllowedException;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisCommand;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisPort;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisResult;
import com.project.bangjjack.domain.post.domain.service.FeatureLabelConverter;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("MatchAnalysisUseCase")
class MatchAnalysisUseCaseTest {

    @Mock
    private UserGetService userGetService;
    @Mock
    private RoommatePostGetService roommatePostGetService;
    @Mock
    private ChecklistGetService checklistGetService;
    @Mock
    private RoommatePreferenceGetService roommatePreferenceGetService;
    @Mock
    private MatchAnalysisPort matchAnalysisPort;
    @Mock
    private FeatureLabelConverter featureLabelConverter;

    @InjectMocks
    private MatchAnalysisUseCase matchAnalysisUseCase;

    private User userWithId(Long id) throws Exception {
        User user = User.create("provider-" + id, "테스트유저" + id, "test" + id + "@gachon.ac.kr", null);
        user.completeOnboarding(2000, 2, Gender.MALE, Campus.GLOBAL_CAMPUS, null, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
        Field idField = user.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, id);
        return user;
    }

    private RoommatePost postOwnedBy(User owner) {
        return RoommatePost.create(
                owner, "룸메이트 구해요", "함께 지낼 룸메이트를 찾습니다.",
                RoomSize.TWO_PERSON, 1, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1
        );
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
    @DisplayName("매칭률 분석 시")
    class AnalyzeMatchRate {

        @Test
        @DisplayName("양측 체크리스트·선호도 등록 완료된 경우 AI 응답을 라벨 변환하여 반환한다")
        void 정상_분석_요청_시_라벨_변환된_응답_반환() throws Exception {
            // given
            Long requesterId = 1L;
            Long authorId = 2L;
            Long postId = 10L;
            User requester = userWithId(requesterId);
            User author = userWithId(authorId);
            RoommatePost post = postOwnedBy(author);
            LifestyleChecklist requesterChecklist = checklistFor(requester);
            LifestyleChecklist authorChecklist = checklistFor(author);

            given(roommatePostGetService.getById(postId)).willReturn(post);
            given(userGetService.getById(requesterId)).willReturn(requester);
            given(checklistGetService.findByUser(requester)).willReturn(Optional.of(requesterChecklist));
            given(checklistGetService.findByUser(author)).willReturn(Optional.of(authorChecklist));
            given(checklistGetService.findSleepHabitsByChecklist(requesterChecklist)).willReturn(List.of());
            given(checklistGetService.findSleepHabitsByChecklist(authorChecklist)).willReturn(List.of());
            given(roommatePreferenceGetService.findByUser(requester)).willReturn(Optional.of(preferenceFor(requester)));
            given(roommatePreferenceGetService.findByUser(author)).willReturn(Optional.of(preferenceFor(author)));
            given(matchAnalysisPort.analyze(any(MatchAnalysisCommand.class)))
                    .willReturn(new MatchAnalysisResult(82, List.of("diff_sleep_time"), List.of("match_smoking", "diff_clean_freq", "match_prio1_sleep")));
            given(featureLabelConverter.toLabels(List.of("diff_sleep_time"))).willReturn(List.of("취침 시간"));
            given(featureLabelConverter.toLabels(List.of("match_smoking", "diff_clean_freq", "match_prio1_sleep")))
                    .willReturn(List.of("흡연 습관", "청소 빈도", "1순위: 수면 패턴"));

            // when
            MatchRateResponse response = matchAnalysisUseCase.analyzeMatchRate(requesterId, postId);

            // then
            assertThat(response.matchRate()).isEqualTo(82);
            assertThat(response.matchedAttributes()).containsExactly("취침 시간");
            assertThat(response.recommendedTopics()).containsExactly("흡연 습관", "청소 빈도", "1순위: 수면 패턴");
        }

        @Test
        @DisplayName("AI 응답의 matchedFeatures가 비어있어도 정상 처리한다")
        void matchedFeatures가_비어있는_케이스() throws Exception {
            // given
            Long requesterId = 1L;
            Long authorId = 2L;
            Long postId = 10L;
            User requester = userWithId(requesterId);
            User author = userWithId(authorId);
            RoommatePost post = postOwnedBy(author);
            LifestyleChecklist requesterChecklist = checklistFor(requester);
            LifestyleChecklist authorChecklist = checklistFor(author);

            given(roommatePostGetService.getById(postId)).willReturn(post);
            given(userGetService.getById(requesterId)).willReturn(requester);
            given(checklistGetService.findByUser(requester)).willReturn(Optional.of(requesterChecklist));
            given(checklistGetService.findByUser(author)).willReturn(Optional.of(authorChecklist));
            given(checklistGetService.findSleepHabitsByChecklist(requesterChecklist)).willReturn(List.of());
            given(checklistGetService.findSleepHabitsByChecklist(authorChecklist)).willReturn(List.of());
            given(roommatePreferenceGetService.findByUser(requester)).willReturn(Optional.of(preferenceFor(requester)));
            given(roommatePreferenceGetService.findByUser(author)).willReturn(Optional.of(preferenceFor(author)));
            given(matchAnalysisPort.analyze(any(MatchAnalysisCommand.class)))
                    .willReturn(new MatchAnalysisResult(50, List.of(), List.of("match_prio2_clean")));
            given(featureLabelConverter.toLabels(List.of())).willReturn(List.of());
            given(featureLabelConverter.toLabels(List.of("match_prio2_clean"))).willReturn(List.of("2순위: 청결도"));

            // when
            MatchRateResponse response = matchAnalysisUseCase.analyzeMatchRate(requesterId, postId);

            // then
            assertThat(response.matchedAttributes()).isEmpty();
            assertThat(response.recommendedTopics()).containsExactly("2순위: 청결도");
        }

        @Test
        @DisplayName("본인이 작성한 모집글이면 SelfMatchNotAllowedException이 발생한다")
        void 본인_글_분석_요청_시_예외_발생() throws Exception {
            // given
            Long userId = 1L;
            Long postId = 10L;
            User user = userWithId(userId);
            RoommatePost post = postOwnedBy(user);
            given(roommatePostGetService.getById(postId)).willReturn(post);

            // when & then
            assertThatThrownBy(() -> matchAnalysisUseCase.analyzeMatchRate(userId, postId))
                    .isInstanceOf(SelfMatchNotAllowedException.class);
        }

        @Test
        @DisplayName("존재하지 않는 postId면 PostNotFoundException이 발생한다")
        void 존재하지_않는_postId_시_예외_발생() {
            // given
            Long requesterId = 1L;
            Long postId = 999L;
            given(roommatePostGetService.getById(postId)).willThrow(PostNotFoundException.class);

            // when & then
            assertThatThrownBy(() -> matchAnalysisUseCase.analyzeMatchRate(requesterId, postId))
                    .isInstanceOf(PostNotFoundException.class);
        }

        @Test
        @DisplayName("요청자 체크리스트 미등록이면 ChecklistNotRegisteredException이 발생한다")
        void 요청자_체크리스트_미등록_시_예외() throws Exception {
            // given
            Long requesterId = 1L;
            Long authorId = 2L;
            Long postId = 10L;
            User requester = userWithId(requesterId);
            User author = userWithId(authorId);
            RoommatePost post = postOwnedBy(author);

            given(roommatePostGetService.getById(postId)).willReturn(post);
            given(userGetService.getById(requesterId)).willReturn(requester);
            given(checklistGetService.findByUser(requester)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> matchAnalysisUseCase.analyzeMatchRate(requesterId, postId))
                    .isInstanceOf(ChecklistNotRegisteredException.class);
        }

        @Test
        @DisplayName("작성자 체크리스트 미등록이면 ChecklistNotRegisteredException이 발생한다")
        void 작성자_체크리스트_미등록_시_예외() throws Exception {
            // given
            Long requesterId = 1L;
            Long authorId = 2L;
            Long postId = 10L;
            User requester = userWithId(requesterId);
            User author = userWithId(authorId);
            RoommatePost post = postOwnedBy(author);
            LifestyleChecklist requesterChecklist = checklistFor(requester);

            given(roommatePostGetService.getById(postId)).willReturn(post);
            given(userGetService.getById(requesterId)).willReturn(requester);
            given(checklistGetService.findByUser(requester)).willReturn(Optional.of(requesterChecklist));
            given(checklistGetService.findSleepHabitsByChecklist(requesterChecklist)).willReturn(List.of());
            given(roommatePreferenceGetService.findByUser(requester)).willReturn(Optional.of(preferenceFor(requester)));
            given(checklistGetService.findByUser(author)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> matchAnalysisUseCase.analyzeMatchRate(requesterId, postId))
                    .isInstanceOf(ChecklistNotRegisteredException.class);
        }

        @Test
        @DisplayName("요청자 선호도 미등록이면 PreferenceNotRegisteredException이 발생한다")
        void 요청자_선호도_미등록_시_예외() throws Exception {
            // given
            Long requesterId = 1L;
            Long authorId = 2L;
            Long postId = 10L;
            User requester = userWithId(requesterId);
            User author = userWithId(authorId);
            RoommatePost post = postOwnedBy(author);
            LifestyleChecklist requesterChecklist = checklistFor(requester);

            given(roommatePostGetService.getById(postId)).willReturn(post);
            given(userGetService.getById(requesterId)).willReturn(requester);
            given(checklistGetService.findByUser(requester)).willReturn(Optional.of(requesterChecklist));
            given(checklistGetService.findSleepHabitsByChecklist(requesterChecklist)).willReturn(List.of());
            given(roommatePreferenceGetService.findByUser(requester)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> matchAnalysisUseCase.analyzeMatchRate(requesterId, postId))
                    .isInstanceOf(PreferenceNotRegisteredException.class);
        }

        @Test
        @DisplayName("AI API 호출 실패 시 AiServiceUnavailableException이 전파된다")
        void AI_API_실패_시_예외_전파() throws Exception {
            // given
            Long requesterId = 1L;
            Long authorId = 2L;
            Long postId = 10L;
            User requester = userWithId(requesterId);
            User author = userWithId(authorId);
            RoommatePost post = postOwnedBy(author);
            LifestyleChecklist requesterChecklist = checklistFor(requester);
            LifestyleChecklist authorChecklist = checklistFor(author);

            given(roommatePostGetService.getById(postId)).willReturn(post);
            given(userGetService.getById(requesterId)).willReturn(requester);
            given(checklistGetService.findByUser(requester)).willReturn(Optional.of(requesterChecklist));
            given(checklistGetService.findByUser(author)).willReturn(Optional.of(authorChecklist));
            given(checklistGetService.findSleepHabitsByChecklist(requesterChecklist)).willReturn(List.of());
            given(checklistGetService.findSleepHabitsByChecklist(authorChecklist)).willReturn(List.of());
            given(roommatePreferenceGetService.findByUser(requester)).willReturn(Optional.of(preferenceFor(requester)));
            given(roommatePreferenceGetService.findByUser(author)).willReturn(Optional.of(preferenceFor(author)));
            given(matchAnalysisPort.analyze(any(MatchAnalysisCommand.class)))
                    .willThrow(AiServiceUnavailableException.class);

            // when & then
            assertThatThrownBy(() -> matchAnalysisUseCase.analyzeMatchRate(requesterId, postId))
                    .isInstanceOf(AiServiceUnavailableException.class);
        }
    }
}
