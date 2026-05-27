package com.project.bangjjack.domain.post.application.loader;

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
import com.project.bangjjack.domain.checklist.domain.service.ChecklistBundle;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistGetService;
import com.project.bangjjack.domain.checklist.domain.service.RoommatePreferenceGetService;
import com.project.bangjjack.domain.post.application.exception.ChecklistNotRegisteredException;
import com.project.bangjjack.domain.post.application.exception.PreferenceNotRegisteredException;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupMemberGetService;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecommendedPostsDataLoader")
class RecommendedPostsDataLoaderTest {

    @Mock
    private UserGetService userGetService;
    @Mock
    private ChecklistGetService checklistGetService;
    @Mock
    private RoommatePreferenceGetService roommatePreferenceGetService;
    @Mock
    private RoommatePostGetService roommatePostGetService;
    @Mock
    private RoommateGroupMemberGetService roommateGroupMemberGetService;

    @InjectMocks
    private RecommendedPostsDataLoader recommendedPostsDataLoader;

    private User userWithId(Long id, Gender gender, Campus campus, Dormitory dormitory) throws Exception {
        User user = User.create("provider-" + id, "유저" + id, "u" + id + "@gachon.ac.kr", null);
        user.completeOnboarding(2000, 2, gender, campus, null, Semester.SIXTEEN_WEEKS, dormitory);
        Field idField = user.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, id);
        return user;
    }

    private User userWithId(Long id) throws Exception {
        return userWithId(id, Gender.FEMALE, Campus.GLOBAL_CAMPUS, Dormitory.DORM_1);
    }

    private RoommatePost postWithId(User owner, Long postId) throws Exception {
        RoommatePost post = RoommatePost.create(
                owner, "룸메 #" + postId, "설명 " + postId,
                RoomSize.TWO_PERSON, 1, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1
        );
        Field idField = post.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(post, postId);
        return post;
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
    @DisplayName("loadBundle")
    class LoadBundle {

        @Test
        @DisplayName("요청자 체크리스트 미등록이면 ChecklistNotRegisteredException이 발생한다")
        void 요청자_체크리스트_미등록_예외() throws Exception {
            // given
            Long userId = 1L;
            User requester = userWithId(userId);
            given(userGetService.getById(userId)).willReturn(requester);
            given(checklistGetService.findByUser(requester)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> recommendedPostsDataLoader.loadBundle(userId, null))
                    .isInstanceOf(ChecklistNotRegisteredException.class);
        }

        @Test
        @DisplayName("요청자 선호도 미등록이면 PreferenceNotRegisteredException이 발생한다")
        void 요청자_선호도_미등록_예외() throws Exception {
            // given
            Long userId = 1L;
            User requester = userWithId(userId);
            LifestyleChecklist requesterChecklist = checklistFor(requester);
            given(userGetService.getById(userId)).willReturn(requester);
            given(checklistGetService.findByUser(requester)).willReturn(Optional.of(requesterChecklist));
            given(checklistGetService.findSleepHabitsByChecklist(requesterChecklist)).willReturn(List.of());
            given(roommatePreferenceGetService.findByUser(requester)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> recommendedPostsDataLoader.loadBundle(userId, null))
                    .isInstanceOf(PreferenceNotRegisteredException.class);
        }

        @Test
        @DisplayName("후보 모집글이 없으면 candidates는 비어 있고 요청자 프로필만 반환한다")
        void 후보_없음() throws Exception {
            // given
            Long userId = 1L;
            User requester = userWithId(userId);
            LifestyleChecklist requesterChecklist = checklistFor(requester);
            given(userGetService.getById(userId)).willReturn(requester);
            given(checklistGetService.findByUser(requester)).willReturn(Optional.of(requesterChecklist));
            given(checklistGetService.findSleepHabitsByChecklist(requesterChecklist)).willReturn(List.of());
            given(roommatePreferenceGetService.findByUser(requester)).willReturn(Optional.of(preferenceFor(requester)));
            given(roommatePostGetService.getRecommendationCandidates(requester, null)).willReturn(List.of());

            // when
            RecommendedPostsBundle bundle = recommendedPostsDataLoader.loadBundle(userId, null);

            // then
            assertThat(bundle.candidates()).isEmpty();
            assertThat(bundle.requesterProfile().user().getId()).isEqualTo(userId);
        }

        @Test
        @DisplayName("RoommatePostGetService에 요청자 User 엔티티 전체가 전달되어 gender/campus/dormitory 필터가 도메인 단에서 적용된다")
        void 요청자_엔티티_그대로_전달() throws Exception {
            // given — 요청자는 MALE / MEDICAL_CAMPUS 가정(임의)
            Long userId = 1L;
            User requester = userWithId(userId, Gender.MALE, Campus.MEDICAL_CAMPUS, Dormitory.DORM_3);
            LifestyleChecklist requesterChecklist = checklistFor(requester);
            given(userGetService.getById(userId)).willReturn(requester);
            given(checklistGetService.findByUser(requester)).willReturn(Optional.of(requesterChecklist));
            given(checklistGetService.findSleepHabitsByChecklist(requesterChecklist)).willReturn(List.of());
            given(roommatePreferenceGetService.findByUser(requester)).willReturn(Optional.of(preferenceFor(requester)));
            given(roommatePostGetService.getRecommendationCandidates(any(User.class), any())).willReturn(List.of());

            // when
            recommendedPostsDataLoader.loadBundle(userId, RoomSize.THREE_PERSON);

            // then — getRecommendationCandidates 가 요청자 User 자체와 roomSize 만 받는지 검증
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(roommatePostGetService).getRecommendationCandidates(userCaptor.capture(), eq(RoomSize.THREE_PERSON));
            User passed = userCaptor.getValue();
            assertThat(passed.getId()).isEqualTo(userId);
            assertThat(passed.getGender()).isEqualTo(Gender.MALE);
            assertThat(passed.getCampus()).isEqualTo(Campus.MEDICAL_CAMPUS);
            assertThat(passed.getDormitory()).isEqualTo(Dormitory.DORM_3);
        }

        @Test
        @DisplayName("후보 작성자 중 체크리스트·선호도 미등록자는 candidates에서 제외된다")
        void 작성자_미등록자_제외() throws Exception {
            // given
            Long userId = 1L;
            User requester = userWithId(userId);
            User authorA = userWithId(10L);
            User authorB = userWithId(11L);
            User authorC = userWithId(12L);
            RoommatePost postA = postWithId(authorA, 100L);
            RoommatePost postB = postWithId(authorB, 101L);
            RoommatePost postC = postWithId(authorC, 102L);

            LifestyleChecklist requesterChecklist = checklistFor(requester);
            given(userGetService.getById(userId)).willReturn(requester);
            given(checklistGetService.findByUser(requester)).willReturn(Optional.of(requesterChecklist));
            given(checklistGetService.findSleepHabitsByChecklist(requesterChecklist)).willReturn(List.of());
            given(roommatePreferenceGetService.findByUser(requester)).willReturn(Optional.of(preferenceFor(requester)));
            given(roommatePostGetService.getRecommendationCandidates(requester, RoomSize.TWO_PERSON))
                    .willReturn(List.of(postA, postB, postC));
            // authorA: 체크리스트+선호도 등록 / authorB: 선호도 미등록 / authorC: 체크리스트 미등록
            ChecklistBundle bundleA = new ChecklistBundle(checklistFor(authorA), List.of());
            ChecklistBundle bundleB = new ChecklistBundle(checklistFor(authorB), List.of());
            given(checklistGetService.getChecklistBundlesByUserIds(anyCollection()))
                    .willReturn(Map.of(10L, bundleA, 11L, bundleB));
            given(roommatePreferenceGetService.getByUserIdIn(anyCollection()))
                    .willReturn(Map.of(10L, preferenceFor(authorA), 12L, preferenceFor(authorC)));
            given(roommateGroupMemberGetService.countActiveByPostIdIn(anyCollection()))
                    .willReturn(Map.of(100L, 1L));

            // when
            RecommendedPostsBundle result = recommendedPostsDataLoader.loadBundle(userId, RoomSize.TWO_PERSON);

            // then
            assertThat(result.candidates()).hasSize(1);
            assertThat(result.candidates().get(0).post().getId()).isEqualTo(100L);
            assertThat(result.candidates().get(0).currentMemberCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("그룹 멤버 카운트가 없는 후보는 currentMemberCount=0으로 표기된다")
        void 그룹_카운트_미스_시_0() throws Exception {
            // given
            Long userId = 1L;
            User requester = userWithId(userId);
            User authorA = userWithId(10L);
            RoommatePost postA = postWithId(authorA, 100L);

            LifestyleChecklist requesterChecklist = checklistFor(requester);
            given(userGetService.getById(userId)).willReturn(requester);
            given(checklistGetService.findByUser(requester)).willReturn(Optional.of(requesterChecklist));
            given(checklistGetService.findSleepHabitsByChecklist(requesterChecklist)).willReturn(List.of());
            given(roommatePreferenceGetService.findByUser(requester)).willReturn(Optional.of(preferenceFor(requester)));
            given(roommatePostGetService.getRecommendationCandidates(requester, null))
                    .willReturn(List.of(postA));
            given(checklistGetService.getChecklistBundlesByUserIds(Set.of(10L)))
                    .willReturn(Map.of(10L, new ChecklistBundle(checklistFor(authorA), List.of())));
            given(roommatePreferenceGetService.getByUserIdIn(Set.of(10L)))
                    .willReturn(Map.of(10L, preferenceFor(authorA)));
            given(roommateGroupMemberGetService.countActiveByPostIdIn(Set.of(100L)))
                    .willReturn(Map.of());

            // when
            RecommendedPostsBundle result = recommendedPostsDataLoader.loadBundle(userId, null);

            // then
            assertThat(result.candidates()).hasSize(1);
            assertThat(result.candidates().get(0).currentMemberCount()).isEqualTo(0);
        }
    }
}
