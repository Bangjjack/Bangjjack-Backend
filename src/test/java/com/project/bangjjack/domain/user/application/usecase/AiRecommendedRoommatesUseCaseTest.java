package com.project.bangjjack.domain.user.application.usecase;

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
import com.project.bangjjack.domain.department.domain.entity.Department;
import com.project.bangjjack.domain.post.application.exception.AiServiceUnavailableException;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisProfile;
import com.project.bangjjack.domain.post.domain.port.match.MatchBatchAnalysisPort;
import com.project.bangjjack.domain.post.domain.port.match.MatchBatchCommand;
import com.project.bangjjack.domain.post.domain.port.match.MatchBatchResult;
import com.project.bangjjack.domain.post.domain.port.match.RankedMatch;
import com.project.bangjjack.domain.post.external.aimatch.AiMatchApiProperties;
import com.project.bangjjack.domain.user.application.dto.response.AiRecommendedRoommateResponse;
import com.project.bangjjack.domain.user.application.loader.CandidateUserEntry;
import com.project.bangjjack.domain.user.application.loader.RecommendedRoommatesBundle;
import com.project.bangjjack.domain.user.application.loader.RecommendedRoommatesDataLoader;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.Gender;
import com.project.bangjjack.domain.user.domain.entity.Semester;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.port.cache.AiRecommendedRoommatesCachePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("AiRecommendedRoommatesUseCase")
class AiRecommendedRoommatesUseCaseTest {

    @Mock
    private AiRecommendedRoommatesCachePort aiRecommendedRoommatesCachePort;
    @Mock
    private RecommendedRoommatesDataLoader recommendedRoommatesDataLoader;
    @Mock
    private MatchBatchAnalysisPort matchBatchAnalysisPort;
    @Mock
    private AiMatchApiProperties aiMatchApiProperties;

    private final Executor syncExecutor = Runnable::run;

    private AiRecommendedRoommatesUseCase aiRecommendedRoommatesUseCase;

    @BeforeEach
    void setUp() {
        aiRecommendedRoommatesUseCase = new AiRecommendedRoommatesUseCase(
                aiRecommendedRoommatesCachePort,
                recommendedRoommatesDataLoader,
                matchBatchAnalysisPort,
                aiMatchApiProperties,
                syncExecutor
        );
    }

    private Department departmentWithId(Long id, String name) throws Exception {
        Department department = Department.create(name, Campus.GLOBAL_CAMPUS);
        Field idField = department.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(department, id);
        return department;
    }

    private User userWithId(Long id, Department department) throws Exception {
        User user = User.create("provider-" + id, "유저" + id, "u" + id + "@gachon.ac.kr",
                "https://img.example.com/" + id);
        user.completeOnboarding(2000 + id.intValue(), 2, Gender.FEMALE, Campus.GLOBAL_CAMPUS,
                department, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
        Field idField = user.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, id);
        return user;
    }

    private LifestyleChecklist checklistFor(User user, Smoking smoking) {
        return LifestyleChecklist.create(user, new LifestyleChecklistRequest(
                Bedtime.BETWEEN_22_24,
                WakeUpTime.BETWEEN_6_8,
                List.of(SleepHabit.NONE),
                CleaningCycle.ONCE_OR_TWICE_A_WEEK,
                DormStayTime.HALF_AND_HALF,
                CallHabit.WHISPER,
                IndoorTemperature.INSENSITIVE,
                NoiseSensitivity.NORMAL,
                smoking
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

    private CandidateUserEntry candidate(Long userId, String departmentName, Smoking smoking) throws Exception {
        Department department = departmentWithId(userId, departmentName);
        User user = userWithId(userId, department);
        MatchAnalysisProfile profile = MatchAnalysisProfile.of(
                user, checklistFor(user, smoking), List.of(), preferenceFor(user)
        );
        return CandidateUserEntry.of(user, profile);
    }

    @Nested
    @DisplayName("getRecommended")
    class GetRecommended {

        @Test
        @DisplayName("캐시 HIT 시 AI 호출/DB 조회 없이 캐시값을 그대로 반환한다")
        void 캐시_히트_시_바로_반환() {
            // given
            Long userId = 1L;
            List<AiRecommendedRoommateResponse> cached = List.of();
            given(aiRecommendedRoommatesCachePort.find(userId)).willReturn(Optional.of(cached));

            // when
            List<AiRecommendedRoommateResponse> result = aiRecommendedRoommatesUseCase.getRecommended(userId);

            // then
            assertThat(result).isSameAs(cached);
            verify(recommendedRoommatesDataLoader, never()).loadBundle(any());
            verify(matchBatchAnalysisPort, never()).analyze(any());
            verify(aiRecommendedRoommatesCachePort, never()).save(any(), any(), any());
        }

        @Test
        @DisplayName("캐시 MISS + 후보 3건이면 AI 응답의 rank 순서대로 변환하여 반환하고 캐시에 저장한다")
        void 캐시_미스_시_AI_호출_후_캐시_저장() throws Exception {
            // given
            Long userId = 1L;
            Department requesterDept = departmentWithId(900L, "요청자학과");
            User requester = userWithId(userId, requesterDept);
            MatchAnalysisProfile requesterProfile = MatchAnalysisProfile.of(
                    requester, checklistFor(requester, Smoking.NON_SMOKER), List.of(), preferenceFor(requester)
            );
            CandidateUserEntry c0 = candidate(100L, "컴퓨터공학과", Smoking.NON_SMOKER);
            CandidateUserEntry c1 = candidate(101L, "경영학과", Smoking.CIGARETTE);
            CandidateUserEntry c2 = candidate(102L, "디자인학과", Smoking.ELECTRONIC_CIGARETTE);
            RecommendedRoommatesBundle bundle = RecommendedRoommatesBundle.of(
                    requesterProfile, List.of(c0, c1, c2)
            );

            MatchBatchResult batchResult = MatchBatchResult.of(List.of(
                    RankedMatch.of(1, 2, 88),
                    RankedMatch.of(2, 0, 75),
                    RankedMatch.of(3, 1, 60)
            ));

            given(aiRecommendedRoommatesCachePort.find(userId)).willReturn(Optional.empty());
            given(recommendedRoommatesDataLoader.loadBundle(userId)).willReturn(bundle);
            given(aiMatchApiProperties.recommendedRoommatesTopK()).willReturn(3);
            given(aiMatchApiProperties.recommendedRoommatesCacheTtl()).willReturn(21600L);
            given(matchBatchAnalysisPort.analyze(any(MatchBatchCommand.class))).willReturn(batchResult);

            // when
            List<AiRecommendedRoommateResponse> result = aiRecommendedRoommatesUseCase.getRecommended(userId);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).userId()).isEqualTo(102L);
            assertThat(result.get(0).matchRate()).isEqualTo(88);
            assertThat(result.get(0).smoking()).isEqualTo(Smoking.ELECTRONIC_CIGARETTE);
            assertThat(result.get(0).departmentName()).isEqualTo("디자인학과");
            assertThat(result.get(0).dormitory()).isEqualTo(Dormitory.DORM_1);
            assertThat(result.get(0).profileImage()).isEqualTo("https://img.example.com/102");
            assertThat(result.get(0).birthYear()).isEqualTo(2102);
            assertThat(result.get(0).roommatePreferences()).containsExactly(
                    RoommatePreferenceFactor.BEDTIME,
                    RoommatePreferenceFactor.CLEANING_HABIT,
                    RoommatePreferenceFactor.NOISE_SENSITIVITY
            );

            assertThat(result.get(1).userId()).isEqualTo(100L);
            assertThat(result.get(1).matchRate()).isEqualTo(75);
            assertThat(result.get(1).departmentName()).isEqualTo("컴퓨터공학과");

            assertThat(result.get(2).userId()).isEqualTo(101L);
            assertThat(result.get(2).matchRate()).isEqualTo(60);
            assertThat(result.get(2).departmentName()).isEqualTo("경영학과");

            verify(aiRecommendedRoommatesCachePort, times(1))
                    .save(eq(userId), eq(result), eq(Duration.ofSeconds(21600L)));
        }

        @Test
        @DisplayName("eligibility 통과 후보가 0건이면 빈 리스트도 캐시에 저장하고 AI는 호출하지 않는다")
        void 후보_0건_시_빈_리스트_캐시_저장() throws Exception {
            // given
            Long userId = 1L;
            Department requesterDept = departmentWithId(900L, "요청자학과");
            User requester = userWithId(userId, requesterDept);
            MatchAnalysisProfile requesterProfile = MatchAnalysisProfile.of(
                    requester, checklistFor(requester, Smoking.NON_SMOKER), List.of(), preferenceFor(requester)
            );
            RecommendedRoommatesBundle bundle = RecommendedRoommatesBundle.of(requesterProfile, List.of());

            given(aiRecommendedRoommatesCachePort.find(userId)).willReturn(Optional.empty());
            given(recommendedRoommatesDataLoader.loadBundle(userId)).willReturn(bundle);
            given(aiMatchApiProperties.recommendedRoommatesCacheTtl()).willReturn(21600L);

            // when
            List<AiRecommendedRoommateResponse> result = aiRecommendedRoommatesUseCase.getRecommended(userId);

            // then
            assertThat(result).isEmpty();
            verify(matchBatchAnalysisPort, never()).analyze(any());
            verify(aiRecommendedRoommatesCachePort, times(1))
                    .save(eq(userId), eq(List.of()), eq(Duration.ofSeconds(21600L)));
        }

        @Test
        @DisplayName("AI 호출 실패 시 AiServiceUnavailableException이 전파되고 캐시는 저장되지 않는다")
        void AI_실패_시_캐시_저장_안_함() throws Exception {
            // given
            Long userId = 1L;
            Department requesterDept = departmentWithId(900L, "요청자학과");
            User requester = userWithId(userId, requesterDept);
            MatchAnalysisProfile requesterProfile = MatchAnalysisProfile.of(
                    requester, checklistFor(requester, Smoking.NON_SMOKER), List.of(), preferenceFor(requester)
            );
            CandidateUserEntry c0 = candidate(100L, "컴퓨터공학과", Smoking.NON_SMOKER);
            RecommendedRoommatesBundle bundle = RecommendedRoommatesBundle.of(requesterProfile, List.of(c0));

            given(aiRecommendedRoommatesCachePort.find(userId)).willReturn(Optional.empty());
            given(recommendedRoommatesDataLoader.loadBundle(userId)).willReturn(bundle);
            given(aiMatchApiProperties.recommendedRoommatesTopK()).willReturn(3);
            given(matchBatchAnalysisPort.analyze(any(MatchBatchCommand.class)))
                    .willThrow(AiServiceUnavailableException.class);

            // when & then
            assertThatThrownBy(() -> aiRecommendedRoommatesUseCase.getRecommended(userId))
                    .isInstanceOf(AiServiceUnavailableException.class);
            verify(aiRecommendedRoommatesCachePort, never()).save(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("getRecommended - 청크 병렬 분기")
    class GetRecommendedParallel {

        @Test
        @DisplayName("candidates.size() <= chunkSize 이면 단일 호출 경로를 탄다")
        void 단일_호출_분기() throws Exception {
            // given
            Long userId = 1L;
            Department requesterDept = departmentWithId(900L, "요청자학과");
            User requester = userWithId(userId, requesterDept);
            MatchAnalysisProfile requesterProfile = MatchAnalysisProfile.of(
                    requester, checklistFor(requester, Smoking.NON_SMOKER), List.of(), preferenceFor(requester)
            );
            List<CandidateUserEntry> candidates = List.of(
                    candidate(100L, "학과0", Smoking.NON_SMOKER),
                    candidate(101L, "학과1", Smoking.NON_SMOKER),
                    candidate(102L, "학과2", Smoking.NON_SMOKER)
            );
            RecommendedRoommatesBundle bundle = RecommendedRoommatesBundle.of(requesterProfile, candidates);

            MatchBatchResult batchResult = MatchBatchResult.of(List.of(
                    RankedMatch.of(1, 0, 90),
                    RankedMatch.of(2, 1, 80),
                    RankedMatch.of(3, 2, 70)
            ));

            given(aiRecommendedRoommatesCachePort.find(userId)).willReturn(Optional.empty());
            given(recommendedRoommatesDataLoader.loadBundle(userId)).willReturn(bundle);
            given(aiMatchApiProperties.recommendedRoommatesTopK()).willReturn(3);
            given(aiMatchApiProperties.recommendedRoommatesChunkSize()).willReturn(5);
            given(aiMatchApiProperties.recommendedRoommatesCacheTtl()).willReturn(21600L);
            given(matchBatchAnalysisPort.analyze(any(MatchBatchCommand.class))).willReturn(batchResult);

            // when
            List<AiRecommendedRoommateResponse> result = aiRecommendedRoommatesUseCase.getRecommended(userId);

            // then
            assertThat(result).hasSize(3);
            verify(matchBatchAnalysisPort, times(1)).analyze(any(MatchBatchCommand.class));
        }

        @Test
        @DisplayName("candidates.size() > chunkSize 이면 청크 수만큼 호출하고 글로벌 매칭률 내림차순으로 머지·재랭킹한다")
        void 청크_병렬_호출_및_머지_재랭킹() throws Exception {
            // given
            Long userId = 1L;
            Department requesterDept = departmentWithId(900L, "요청자학과");
            User requester = userWithId(userId, requesterDept);
            MatchAnalysisProfile requesterProfile = MatchAnalysisProfile.of(
                    requester, checklistFor(requester, Smoking.NON_SMOKER), List.of(), preferenceFor(requester)
            );
            // 6 candidates, chunkSize=3 → 2 chunks: [c0,c1,c2], [c3,c4,c5]
            List<CandidateUserEntry> candidates = List.of(
                    candidate(100L, "학과0", Smoking.NON_SMOKER),
                    candidate(101L, "학과1", Smoking.NON_SMOKER),
                    candidate(102L, "학과2", Smoking.NON_SMOKER),
                    candidate(103L, "학과3", Smoking.NON_SMOKER),
                    candidate(104L, "학과4", Smoking.NON_SMOKER),
                    candidate(105L, "학과5", Smoking.NON_SMOKER)
            );
            RecommendedRoommatesBundle bundle = RecommendedRoommatesBundle.of(requesterProfile, candidates);

            // chunk0 candidateIndex(local) → c0/c1/c2 = matchRate 90/80/70
            MatchBatchResult chunk0Result = MatchBatchResult.of(List.of(
                    RankedMatch.of(1, 0, 90),
                    RankedMatch.of(2, 1, 80),
                    RankedMatch.of(3, 2, 70)
            ));
            // chunk1 candidateIndex(local) → c3/c4/c5 = matchRate 95/85/60
            MatchBatchResult chunk1Result = MatchBatchResult.of(List.of(
                    RankedMatch.of(1, 0, 95),
                    RankedMatch.of(2, 1, 85),
                    RankedMatch.of(3, 2, 60)
            ));

            given(aiRecommendedRoommatesCachePort.find(userId)).willReturn(Optional.empty());
            given(recommendedRoommatesDataLoader.loadBundle(userId)).willReturn(bundle);
            given(aiMatchApiProperties.recommendedRoommatesTopK()).willReturn(3);
            given(aiMatchApiProperties.recommendedRoommatesChunkSize()).willReturn(3);
            given(aiMatchApiProperties.recommendedRoommatesCacheTtl()).willReturn(21600L);
            given(matchBatchAnalysisPort.analyze(any(MatchBatchCommand.class)))
                    .willReturn(chunk0Result, chunk1Result);

            // when
            List<AiRecommendedRoommateResponse> result = aiRecommendedRoommatesUseCase.getRecommended(userId);

            // then
            // 머지 후 글로벌 정렬: 95(c3) > 90(c0) > 85(c4) > 80(c1) > 70(c2) > 60(c5)
            // topK=3 → c3, c0, c4
            assertThat(result).hasSize(3);
            assertThat(result.get(0).userId()).isEqualTo(103L);
            assertThat(result.get(0).matchRate()).isEqualTo(95);
            assertThat(result.get(1).userId()).isEqualTo(100L);
            assertThat(result.get(1).matchRate()).isEqualTo(90);
            assertThat(result.get(2).userId()).isEqualTo(104L);
            assertThat(result.get(2).matchRate()).isEqualTo(85);

            verify(matchBatchAnalysisPort, times(2)).analyze(any(MatchBatchCommand.class));
            verify(aiRecommendedRoommatesCachePort, times(1))
                    .save(eq(userId), eq(result), eq(Duration.ofSeconds(21600L)));
        }

        @Test
        @DisplayName("청크 호출 시 각 청크의 candidates 와 topK 가 chunkSize 단위로 분할되어 전달된다")
        void 청크_command_분할_검증() throws Exception {
            // given
            Long userId = 1L;
            Department requesterDept = departmentWithId(900L, "요청자학과");
            User requester = userWithId(userId, requesterDept);
            MatchAnalysisProfile requesterProfile = MatchAnalysisProfile.of(
                    requester, checklistFor(requester, Smoking.NON_SMOKER), List.of(), preferenceFor(requester)
            );
            // 5 candidates, chunkSize=2 → 3 chunks: size 2, 2, 1
            List<CandidateUserEntry> candidates = List.of(
                    candidate(100L, "학과0", Smoking.NON_SMOKER),
                    candidate(101L, "학과1", Smoking.NON_SMOKER),
                    candidate(102L, "학과2", Smoking.NON_SMOKER),
                    candidate(103L, "학과3", Smoking.NON_SMOKER),
                    candidate(104L, "학과4", Smoking.NON_SMOKER)
            );
            RecommendedRoommatesBundle bundle = RecommendedRoommatesBundle.of(requesterProfile, candidates);

            given(aiRecommendedRoommatesCachePort.find(userId)).willReturn(Optional.empty());
            given(recommendedRoommatesDataLoader.loadBundle(userId)).willReturn(bundle);
            given(aiMatchApiProperties.recommendedRoommatesTopK()).willReturn(3);
            given(aiMatchApiProperties.recommendedRoommatesChunkSize()).willReturn(2);
            given(aiMatchApiProperties.recommendedRoommatesCacheTtl()).willReturn(21600L);
            given(matchBatchAnalysisPort.analyze(any(MatchBatchCommand.class)))
                    .willReturn(MatchBatchResult.of(List.of()));

            // when
            aiRecommendedRoommatesUseCase.getRecommended(userId);

            // then
            ArgumentCaptor<MatchBatchCommand> captor = ArgumentCaptor.forClass(MatchBatchCommand.class);
            verify(matchBatchAnalysisPort, times(3)).analyze(captor.capture());
            List<MatchBatchCommand> commands = captor.getAllValues();
            assertThat(commands.get(0).candidates()).hasSize(2);
            assertThat(commands.get(0).topK()).isEqualTo(2); // min(chunkSize, topK)
            assertThat(commands.get(1).candidates()).hasSize(2);
            assertThat(commands.get(1).topK()).isEqualTo(2);
            assertThat(commands.get(2).candidates()).hasSize(1);
            assertThat(commands.get(2).topK()).isEqualTo(1); // min(remainingChunkSize=1, topK=3)
        }

        @Test
        @DisplayName("청크 중 하나가 실패하면 AiServiceUnavailableException이 전파되고 캐시는 저장되지 않는다")
        void 청크_실패_전파() throws Exception {
            // given
            Long userId = 1L;
            Department requesterDept = departmentWithId(900L, "요청자학과");
            User requester = userWithId(userId, requesterDept);
            MatchAnalysisProfile requesterProfile = MatchAnalysisProfile.of(
                    requester, checklistFor(requester, Smoking.NON_SMOKER), List.of(), preferenceFor(requester)
            );
            List<CandidateUserEntry> candidates = List.of(
                    candidate(100L, "학과0", Smoking.NON_SMOKER),
                    candidate(101L, "학과1", Smoking.NON_SMOKER),
                    candidate(102L, "학과2", Smoking.NON_SMOKER),
                    candidate(103L, "학과3", Smoking.NON_SMOKER)
            );
            RecommendedRoommatesBundle bundle = RecommendedRoommatesBundle.of(requesterProfile, candidates);

            given(aiRecommendedRoommatesCachePort.find(userId)).willReturn(Optional.empty());
            given(recommendedRoommatesDataLoader.loadBundle(userId)).willReturn(bundle);
            given(aiMatchApiProperties.recommendedRoommatesTopK()).willReturn(3);
            given(aiMatchApiProperties.recommendedRoommatesChunkSize()).willReturn(2);
            given(matchBatchAnalysisPort.analyze(any(MatchBatchCommand.class)))
                    .willReturn(MatchBatchResult.of(List.of(RankedMatch.of(1, 0, 80))))
                    .willThrow(AiServiceUnavailableException.class);

            // when & then
            assertThatThrownBy(() -> aiRecommendedRoommatesUseCase.getRecommended(userId))
                    .isInstanceOf(AiServiceUnavailableException.class);
            verify(aiRecommendedRoommatesCachePort, never()).save(any(), any(), any());
        }
    }
}
