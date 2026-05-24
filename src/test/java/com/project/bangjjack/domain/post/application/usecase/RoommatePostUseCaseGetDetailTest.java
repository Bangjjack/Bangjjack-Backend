package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.bookmark.domain.service.BookmarkGetService;
import com.project.bangjjack.domain.checklist.domain.entity.Bedtime;
import com.project.bangjjack.domain.checklist.domain.entity.CallHabit;
import com.project.bangjjack.domain.checklist.domain.entity.CleaningCycle;
import com.project.bangjjack.domain.checklist.domain.entity.DormStayTime;
import com.project.bangjjack.domain.checklist.domain.entity.IndoorTemperature;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklist;
import com.project.bangjjack.domain.checklist.domain.entity.LifestyleChecklistSleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.NoiseSensitivity;
import com.project.bangjjack.domain.checklist.domain.entity.RoommatePreference;
import com.project.bangjjack.domain.checklist.domain.entity.SleepHabit;
import com.project.bangjjack.domain.checklist.domain.entity.Smoking;
import com.project.bangjjack.domain.checklist.domain.entity.WakeUpTime;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistBundle;
import com.project.bangjjack.domain.checklist.domain.service.ChecklistGetService;
import com.project.bangjjack.domain.checklist.domain.service.RoommatePreferenceGetService;
import com.project.bangjjack.domain.post.application.dto.response.RoommateMemberDetailResponse;
import com.project.bangjjack.domain.post.application.dto.response.RoommatePostDetailResponse;
import com.project.bangjjack.domain.post.application.exception.PostNotFoundException;
import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.roommategroup.domain.entity.GroupMemberRole;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupMemberGetService;
import com.project.bangjjack.domain.user.domain.entity.User;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.project.bangjjack.domain.post.application.usecase.RoommatePostFixture.postOwnedBy;
import static com.project.bangjjack.domain.post.application.usecase.RoommatePostFixture.preferenceFor;
import static com.project.bangjjack.domain.post.application.usecase.RoommatePostFixture.sharedLifestyleFor;
import static com.project.bangjjack.domain.post.application.usecase.RoommatePostFixture.userWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoommatePostUseCaseGetDetailTest {

    @Mock
    private RoommatePostGetService roommatePostGetService;

    @Mock
    private BookmarkGetService bookmarkGetService;

    @Mock
    private RoommateGroupMemberGetService roommateGroupMemberGetService;

    @Mock
    private RoommatePreferenceGetService roommatePreferenceGetService;

    @Mock
    private ChecklistGetService checklistGetService;

    @InjectMocks
    private RoommatePostUseCase roommatePostUseCase;

    private static RoommateGroupMember mockMember(User user, GroupMemberRole role) {
        RoommateGroupMember member = mock(RoommateGroupMember.class);
        lenient().when(member.getUser()).thenReturn(user);
        lenient().when(member.getRole()).thenReturn(role);
        return member;
    }

    private static ChecklistBundle bundleOf(User user, List<SleepHabit> sleepHabits) {
        LifestyleChecklist checklist = mock(LifestyleChecklist.class);
        lenient().when(checklist.getUser()).thenReturn(user);
        lenient().when(checklist.getBedtime()).thenReturn(Bedtime.BETWEEN_22_24);
        lenient().when(checklist.getWakeUpTime()).thenReturn(WakeUpTime.BETWEEN_6_8);
        lenient().when(checklist.getCleaningCycle()).thenReturn(CleaningCycle.ONCE_OR_TWICE_A_WEEK);
        lenient().when(checklist.getDormStayTime()).thenReturn(DormStayTime.HALF_AND_HALF);
        lenient().when(checklist.getCallHabit()).thenReturn(CallHabit.OUTSIDE_ONLY);
        lenient().when(checklist.getIndoorTemperature()).thenReturn(IndoorTemperature.INSENSITIVE);
        lenient().when(checklist.getNoiseSensitivity()).thenReturn(NoiseSensitivity.NORMAL);
        lenient().when(checklist.getSmoking()).thenReturn(Smoking.NON_SMOKER);
        List<LifestyleChecklistSleepHabit> habitEntities = sleepHabits.stream().map(habit -> {
            LifestyleChecklistSleepHabit entity = mock(LifestyleChecklistSleepHabit.class);
            lenient().when(entity.getSleepHabit()).thenReturn(habit);
            return entity;
        }).toList();
        return new ChecklistBundle(checklist, habitEntities);
    }

    @Nested
    @DisplayName("모집글 단건 조회 시")
    class GetPostDetail {

        @Test
        @DisplayName("작성자 본인이 조회하면 isOwner=true인 응답을 반환한다")
        void 작성자_본인_조회_시_isOwner_true() {
            // given
            Long userId = 1L;
            User owner = userWithId(userId);
            RoommatePost post = postOwnedBy(owner);
            PostSharedLifestyle sharedLifestyle = sharedLifestyleFor(post);
            List<RoommateGroupMember> members = List.of(mockMember(owner, GroupMemberRole.LEADER));
            RoommatePreference preference = preferenceFor(owner);

            given(roommatePostGetService.getSharedLifestyleWithPostAndUserByPostId(1L)).willReturn(sharedLifestyle);
            given(bookmarkGetService.existsActiveBookmark(userId, 1L)).willReturn(false);
            given(roommateGroupMemberGetService.getActiveMembersWithUserByPostId(1L)).willReturn(members);
            given(checklistGetService.getChecklistBundlesByUserIds(anyCollection())).willReturn(Map.of());
            given(roommatePreferenceGetService.getByUser(owner)).willReturn(preference);

            // when
            RoommatePostDetailResponse response = roommatePostUseCase.getPostDetail(userId, 1L);

            // then
            assertThat(response.isOwner()).isTrue();
            assertThat(response.title()).isEqualTo("룸메이트 구해요");
            assertThat(response.author().authorId()).isEqualTo(userId);
        }

        @Test
        @DisplayName("타인이 조회하면 isOwner=false인 응답을 반환한다")
        void 타인_조회_시_isOwner_false() {
            // given
            Long ownerId = 1L;
            Long viewerId = 2L;
            User owner = userWithId(ownerId);
            RoommatePost post = postOwnedBy(owner);
            PostSharedLifestyle sharedLifestyle = sharedLifestyleFor(post);
            List<RoommateGroupMember> members = List.of(mockMember(owner, GroupMemberRole.LEADER));
            RoommatePreference preference = preferenceFor(owner);

            given(roommatePostGetService.getSharedLifestyleWithPostAndUserByPostId(1L)).willReturn(sharedLifestyle);
            given(bookmarkGetService.existsActiveBookmark(viewerId, 1L)).willReturn(false);
            given(roommateGroupMemberGetService.getActiveMembersWithUserByPostId(1L)).willReturn(members);
            given(checklistGetService.getChecklistBundlesByUserIds(anyCollection())).willReturn(Map.of());
            given(roommatePreferenceGetService.getByUser(owner)).willReturn(preference);

            // when
            RoommatePostDetailResponse response = roommatePostUseCase.getPostDetail(viewerId, 1L);

            // then
            assertThat(response.isOwner()).isFalse();
        }

        @Test
        @DisplayName("북마크한 게시글 조회 시 isBookmarked=true인 응답을 반환한다")
        void 북마크한_게시글_조회_시_isBookmarked_true() {
            // given
            Long userId = 1L;
            User owner = userWithId(userId);
            RoommatePost post = postOwnedBy(owner);
            PostSharedLifestyle sharedLifestyle = sharedLifestyleFor(post);
            RoommatePreference preference = preferenceFor(owner);

            given(roommatePostGetService.getSharedLifestyleWithPostAndUserByPostId(1L)).willReturn(sharedLifestyle);
            given(bookmarkGetService.existsActiveBookmark(userId, 1L)).willReturn(true);
            given(checklistGetService.getChecklistBundlesByUserIds(anyCollection())).willReturn(Map.of());
            given(roommatePreferenceGetService.getByUser(owner)).willReturn(preference);

            // when
            RoommatePostDetailResponse response = roommatePostUseCase.getPostDetail(userId, 1L);

            // then
            assertThat(response.isBookmarked()).isTrue();
        }

        @Test
        @DisplayName("북마크하지 않은 게시글 조회 시 isBookmarked=false인 응답을 반환한다")
        void 북마크하지_않은_게시글_조회_시_isBookmarked_false() {
            // given
            Long userId = 1L;
            User owner = userWithId(userId);
            RoommatePost post = postOwnedBy(owner);
            PostSharedLifestyle sharedLifestyle = sharedLifestyleFor(post);
            RoommatePreference preference = preferenceFor(owner);

            given(roommatePostGetService.getSharedLifestyleWithPostAndUserByPostId(1L)).willReturn(sharedLifestyle);
            given(bookmarkGetService.existsActiveBookmark(userId, 1L)).willReturn(false);
            given(checklistGetService.getChecklistBundlesByUserIds(anyCollection())).willReturn(Map.of());
            given(roommatePreferenceGetService.getByUser(owner)).willReturn(preference);

            // when
            RoommatePostDetailResponse response = roommatePostUseCase.getPostDetail(userId, 1L);

            // then
            assertThat(response.isBookmarked()).isFalse();
        }

        @Test
        @DisplayName("응답에 그룹 활성 멤버(LEADER + MEMBER)들이 userId/username/profileImage/role로 매핑되어 포함된다")
        void 응답에_그룹_멤버_목록_포함() {
            // given
            Long ownerId = 1L;
            User owner = userWithId(ownerId);
            User member1 = userWithId(2L);
            User member2 = userWithId(3L);
            RoommatePost post = postOwnedBy(owner);
            PostSharedLifestyle sharedLifestyle = sharedLifestyleFor(post);
            RoommateGroupMember leader = mockMember(owner, GroupMemberRole.LEADER);
            RoommateGroupMember m1 = mockMember(member1, GroupMemberRole.MEMBER);
            RoommateGroupMember m2 = mockMember(member2, GroupMemberRole.MEMBER);
            List<RoommateGroupMember> members = List.of(leader, m1, m2);

            given(roommatePostGetService.getSharedLifestyleWithPostAndUserByPostId(1L)).willReturn(sharedLifestyle);
            given(roommateGroupMemberGetService.getActiveMembersWithUserByPostId(1L)).willReturn(members);
            given(checklistGetService.getChecklistBundlesByUserIds(anyCollection())).willReturn(Map.of());
            given(roommatePreferenceGetService.getByUser(owner)).willReturn(preferenceFor(owner));

            // when
            RoommatePostDetailResponse response = roommatePostUseCase.getPostDetail(ownerId, 1L);

            // then
            assertThat(response.members())
                    .extracting(RoommateMemberDetailResponse::userId, RoommateMemberDetailResponse::username,
                            RoommateMemberDetailResponse::profileImage, RoommateMemberDetailResponse::role)
                    .containsExactly(
                            Tuple.tuple(1L, owner.getUsername(), owner.getProfileImage(), GroupMemberRole.LEADER),
                            Tuple.tuple(2L, member1.getUsername(), member1.getProfileImage(), GroupMemberRole.MEMBER),
                            Tuple.tuple(3L, member2.getUsername(), member2.getProfileImage(), GroupMemberRole.MEMBER)
                    );
        }

        @Test
        @DisplayName("LEADER 단독 그룹 조회 시 멤버 목록은 LEADER 1건만 포함된다")
        void LEADER_단독_그룹_멤버_1건() {
            // given
            Long ownerId = 1L;
            User owner = userWithId(ownerId);
            RoommatePost post = postOwnedBy(owner);
            PostSharedLifestyle sharedLifestyle = sharedLifestyleFor(post);
            List<RoommateGroupMember> members = List.of(mockMember(owner, GroupMemberRole.LEADER));

            given(roommatePostGetService.getSharedLifestyleWithPostAndUserByPostId(1L)).willReturn(sharedLifestyle);
            given(roommateGroupMemberGetService.getActiveMembersWithUserByPostId(1L)).willReturn(members);
            given(checklistGetService.getChecklistBundlesByUserIds(anyCollection())).willReturn(Map.of());
            given(roommatePreferenceGetService.getByUser(owner)).willReturn(preferenceFor(owner));

            // when
            RoommatePostDetailResponse response = roommatePostUseCase.getPostDetail(ownerId, 1L);

            // then
            assertThat(response.members()).hasSize(1);
            assertThat(response.members().getFirst().role()).isEqualTo(GroupMemberRole.LEADER);
            assertThat(response.members().getFirst().userId()).isEqualTo(ownerId);
        }

        @Test
        @DisplayName("체크리스트 등록한 멤버는 9개 ChecklistField + sleepHabits.value 리스트가 채워져 응답된다")
        void 체크리스트_등록_멤버_응답_매핑() {
            // given
            Long ownerId = 1L;
            User owner = userWithId(ownerId);
            User member1 = userWithId(2L);
            RoommatePost post = postOwnedBy(owner);
            PostSharedLifestyle sharedLifestyle = sharedLifestyleFor(post);
            RoommateGroupMember leader = mockMember(owner, GroupMemberRole.LEADER);
            RoommateGroupMember m1 = mockMember(member1, GroupMemberRole.MEMBER);
            List<RoommateGroupMember> members = List.of(leader, m1);
            ChecklistBundle ownerBundle = bundleOf(owner, List.of(SleepHabit.TOSS_AND_TURN, SleepHabit.SNORING));

            given(roommatePostGetService.getSharedLifestyleWithPostAndUserByPostId(1L)).willReturn(sharedLifestyle);
            given(roommateGroupMemberGetService.getActiveMembersWithUserByPostId(1L)).willReturn(members);
            given(checklistGetService.getChecklistBundlesByUserIds(anyCollection()))
                    .willReturn(Map.of(ownerId, ownerBundle));
            given(roommatePreferenceGetService.getByUser(owner)).willReturn(preferenceFor(owner));

            // when
            RoommatePostDetailResponse response = roommatePostUseCase.getPostDetail(ownerId, 1L);

            // then
            assertThat(response.members()).hasSize(2);
            RoommateMemberDetailResponse leaderResponse = response.members().getFirst();
            assertThat(leaderResponse.lifestyleChecklist()).isNotNull();
            assertThat(leaderResponse.lifestyleChecklist().bedtime().value()).isEqualTo(Bedtime.BETWEEN_22_24);
            assertThat(leaderResponse.lifestyleChecklist().sleepHabits().value())
                    .containsExactly(SleepHabit.TOSS_AND_TURN, SleepHabit.SNORING);
        }

        @Test
        @DisplayName("체크리스트 미등록 멤버는 lifestyleChecklist=null로 응답된다")
        void 체크리스트_미등록_멤버_null_매핑() {
            // given
            Long ownerId = 1L;
            User owner = userWithId(ownerId);
            User member1 = userWithId(2L);
            RoommatePost post = postOwnedBy(owner);
            PostSharedLifestyle sharedLifestyle = sharedLifestyleFor(post);
            RoommateGroupMember leader = mockMember(owner, GroupMemberRole.LEADER);
            RoommateGroupMember m1 = mockMember(member1, GroupMemberRole.MEMBER);
            List<RoommateGroupMember> members = List.of(leader, m1);
            ChecklistBundle ownerBundle = bundleOf(owner, List.of(SleepHabit.NONE));

            given(roommatePostGetService.getSharedLifestyleWithPostAndUserByPostId(1L)).willReturn(sharedLifestyle);
            given(roommateGroupMemberGetService.getActiveMembersWithUserByPostId(1L)).willReturn(members);
            given(checklistGetService.getChecklistBundlesByUserIds(anyCollection()))
                    .willReturn(Map.of(ownerId, ownerBundle));
            given(roommatePreferenceGetService.getByUser(owner)).willReturn(preferenceFor(owner));

            // when
            RoommatePostDetailResponse response = roommatePostUseCase.getPostDetail(ownerId, 1L);

            // then
            assertThat(response.members()).hasSize(2);
            assertThat(response.members().get(0).lifestyleChecklist()).isNotNull();
            assertThat(response.members().get(1).lifestyleChecklist()).isNull();
        }

        @Test
        @DisplayName("체크리스트 일괄 조회는 활성 멤버의 userId 전체와 viewer userId를 전달한다")
        void 체크리스트_일괄_조회_userIds_전달() {
            // given
            Long viewerId = 99L;
            Long ownerId = 1L;
            User owner = userWithId(ownerId);
            User member1 = userWithId(2L);
            User member2 = userWithId(3L);
            RoommatePost post = postOwnedBy(owner);
            PostSharedLifestyle sharedLifestyle = sharedLifestyleFor(post);
            List<RoommateGroupMember> members = List.of(
                    mockMember(owner, GroupMemberRole.LEADER),
                    mockMember(member1, GroupMemberRole.MEMBER),
                    mockMember(member2, GroupMemberRole.MEMBER)
            );

            given(roommatePostGetService.getSharedLifestyleWithPostAndUserByPostId(1L)).willReturn(sharedLifestyle);
            given(roommateGroupMemberGetService.getActiveMembersWithUserByPostId(1L)).willReturn(members);
            given(checklistGetService.getChecklistBundlesByUserIds(anyCollection())).willReturn(Map.of());
            given(roommatePreferenceGetService.getByUser(owner)).willReturn(preferenceFor(owner));

            // when
            roommatePostUseCase.getPostDetail(viewerId, 1L);

            // then
            ArgumentCaptor<Collection<Long>> userIdsCaptor = ArgumentCaptor.forClass(Collection.class);
            verify(checklistGetService).getChecklistBundlesByUserIds(userIdsCaptor.capture());
            assertThat(userIdsCaptor.getValue()).containsExactlyInAnyOrder(1L, 2L, 3L, viewerId);
        }

        @Test
        @DisplayName("존재하지 않는 postId로 조회하면 PostNotFoundException이 발생한다")
        void 존재하지_않는_모집글_조회_시_예외_발생() {
            // given
            Long nonExistentPostId = 999L;
            given(roommatePostGetService.getSharedLifestyleWithPostAndUserByPostId(nonExistentPostId))
                    .willThrow(PostNotFoundException.class);

            // when & then
            assertThatThrownBy(() -> roommatePostUseCase.getPostDetail(1L, nonExistentPostId))
                    .isInstanceOf(PostNotFoundException.class);
        }
    }
}
