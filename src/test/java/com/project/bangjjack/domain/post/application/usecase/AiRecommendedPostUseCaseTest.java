package com.project.bangjjack.domain.post.application.usecase;

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
import com.project.bangjjack.domain.post.application.dto.response.AiRecommendedPostResponse;
import com.project.bangjjack.domain.post.application.exception.AiServiceUnavailableException;
import com.project.bangjjack.domain.post.application.loader.CandidateEntry;
import com.project.bangjjack.domain.post.application.loader.RecommendedPostsBundle;
import com.project.bangjjack.domain.post.application.loader.RecommendedPostsDataLoader;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisProfile;
import com.project.bangjjack.domain.post.domain.port.match.MatchBatchAnalysisPort;
import com.project.bangjjack.domain.post.domain.port.match.MatchBatchCommand;
import com.project.bangjjack.domain.post.domain.port.match.MatchBatchResult;
import com.project.bangjjack.domain.post.domain.port.match.RankedMatch;
import com.project.bangjjack.domain.post.external.aimatch.AiMatchApiProperties;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.Gender;
import com.project.bangjjack.domain.user.domain.entity.Semester;
import com.project.bangjjack.domain.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("AiRecommendedPostUseCase")
class AiRecommendedPostUseCaseTest {

    @Mock
    private RecommendedPostsDataLoader recommendedPostsDataLoader;
    @Mock
    private MatchBatchAnalysisPort matchBatchAnalysisPort;
    @Mock
    private AiMatchApiProperties aiMatchApiProperties;

    @InjectMocks
    private AiRecommendedPostUseCase aiRecommendedPostUseCase;

    private User userWithId(Long id) throws Exception {
        User user = User.create("provider-" + id, "유저" + id, "u" + id + "@gachon.ac.kr", null);
        user.completeOnboarding(2000, 2, Gender.FEMALE, Campus.GLOBAL_CAMPUS, null, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
        Field idField = user.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, id);
        return user;
    }

    private RoommatePost postWithId(User owner, Long postId) throws Exception {
        RoommatePost post = RoommatePost.create(
                owner, "룸메 구해요 #" + postId, "설명 " + postId,
                RoomSize.TWO_PERSON, 1, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1
        );
        Field idField = post.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(post, postId);
        return post;
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

    private CandidateEntry candidate(Long authorId, Long postId, Smoking smoking, int memberCount) throws Exception {
        User author = userWithId(authorId);
        RoommatePost post = postWithId(author, postId);
        MatchAnalysisProfile profile = MatchAnalysisProfile.of(
                author, checklistFor(author, smoking), List.of(), preferenceFor(author)
        );
        return CandidateEntry.of(post, profile, memberCount);
    }

    @Nested
    @DisplayName("getRecommended")
    class GetRecommended {

        @Test
        @DisplayName("후보 3건이면 AI 응답의 rank 순서대로 변환하여 반환한다")
        void 후보_조회_후_AI_호출하여_변환() throws Exception {
            // given
            Long userId = 1L;
            RoomSize roomSize = null;
            User requester = userWithId(userId);
            MatchAnalysisProfile requesterProfile = MatchAnalysisProfile.of(
                    requester, checklistFor(requester, Smoking.NON_SMOKER), List.of(), preferenceFor(requester)
            );
            CandidateEntry c0 = candidate(100L, 1000L, Smoking.NON_SMOKER, 1);
            CandidateEntry c1 = candidate(101L, 1001L, Smoking.CIGARETTE, 2);
            CandidateEntry c2 = candidate(102L, 1002L, Smoking.ELECTRONIC_CIGARETTE, 1);
            RecommendedPostsBundle bundle = RecommendedPostsBundle.of(requesterProfile, List.of(c0, c1, c2));

            MatchBatchResult batchResult = MatchBatchResult.of(List.of(
                    RankedMatch.of(1, 2, 88),
                    RankedMatch.of(2, 0, 75),
                    RankedMatch.of(3, 1, 60)
            ));

            given(recommendedPostsDataLoader.loadBundle(userId, roomSize)).willReturn(bundle);
            given(aiMatchApiProperties.recommendedTopK()).willReturn(10);
            given(matchBatchAnalysisPort.analyze(any(MatchBatchCommand.class))).willReturn(batchResult);

            // when
            List<AiRecommendedPostResponse> result = aiRecommendedPostUseCase.getRecommended(userId, roomSize);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).postId()).isEqualTo(1002L);
            assertThat(result.get(0).matchRate()).isEqualTo(88);
            assertThat(result.get(0).smoking()).isEqualTo(Smoking.ELECTRONIC_CIGARETTE);
            assertThat(result.get(0).currentMemberCount()).isEqualTo(1);
            // totalMemberCount 는 RoomSize 에서 도출 — 모든 후보 post 가 TWO_PERSON 이므로 2
            assertThat(result.get(0).totalMemberCount()).isEqualTo(2);
            assertThat(result.get(0).roomSize()).isEqualTo(RoomSize.TWO_PERSON);
            assertThat(result.get(1).postId()).isEqualTo(1000L);
            assertThat(result.get(1).matchRate()).isEqualTo(75);
            assertThat(result.get(1).totalMemberCount()).isEqualTo(2);
            assertThat(result.get(2).postId()).isEqualTo(1001L);
            assertThat(result.get(2).matchRate()).isEqualTo(60);
            assertThat(result.get(2).totalMemberCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("eligibility 통과 후보가 0건이면 빈 리스트를 반환하고 AI는 호출하지 않는다")
        void 후보_0건_시_빈_리스트_반환() throws Exception {
            // given
            Long userId = 1L;
            User requester = userWithId(userId);
            MatchAnalysisProfile requesterProfile = MatchAnalysisProfile.of(
                    requester, checklistFor(requester, Smoking.NON_SMOKER), List.of(), preferenceFor(requester)
            );
            RecommendedPostsBundle bundle = RecommendedPostsBundle.of(requesterProfile, List.of());

            given(recommendedPostsDataLoader.loadBundle(userId, null)).willReturn(bundle);

            // when
            List<AiRecommendedPostResponse> result = aiRecommendedPostUseCase.getRecommended(userId, null);

            // then
            assertThat(result).isEmpty();
            verify(matchBatchAnalysisPort, never()).analyze(any());
        }

        @Test
        @DisplayName("AI 호출 실패 시 AiServiceUnavailableException이 전파된다")
        void AI_실패_시_예외_전파() throws Exception {
            // given
            Long userId = 1L;
            User requester = userWithId(userId);
            MatchAnalysisProfile requesterProfile = MatchAnalysisProfile.of(
                    requester, checklistFor(requester, Smoking.NON_SMOKER), List.of(), preferenceFor(requester)
            );
            CandidateEntry c0 = candidate(100L, 1000L, Smoking.NON_SMOKER, 1);
            RecommendedPostsBundle bundle = RecommendedPostsBundle.of(requesterProfile, List.of(c0));

            given(recommendedPostsDataLoader.loadBundle(userId, null)).willReturn(bundle);
            given(aiMatchApiProperties.recommendedTopK()).willReturn(10);
            given(matchBatchAnalysisPort.analyze(any(MatchBatchCommand.class)))
                    .willThrow(AiServiceUnavailableException.class);

            // when & then
            assertThatThrownBy(() -> aiRecommendedPostUseCase.getRecommended(userId, null))
                    .isInstanceOf(AiServiceUnavailableException.class);
        }
    }
}
