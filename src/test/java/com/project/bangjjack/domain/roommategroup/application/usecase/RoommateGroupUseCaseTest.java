package com.project.bangjjack.domain.roommategroup.application.usecase;

import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostDeleteService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.roommategroup.application.dto.response.GroupMemberResponse;
import com.project.bangjjack.domain.roommategroup.application.dto.response.MyRoommateGroupResponse;
import com.project.bangjjack.domain.roommategroup.application.event.RoommateGroupDisbandedEvent;
import com.project.bangjjack.domain.roommategroup.application.exception.RoommateGroupMembershipNotFoundException;
import com.project.bangjjack.domain.roommategroup.domain.entity.GroupMemberRole;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroup;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupDeleteService;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupMemberDeleteService;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupMemberGetService;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.Semester;
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
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoommateGroupUseCase")
class RoommateGroupUseCaseTest {

    @Mock
    private RoommateGroupMemberGetService roommateGroupMemberGetService;

    @Mock
    private RoommateGroupMemberDeleteService roommateGroupMemberDeleteService;

    @Mock
    private RoommateGroupDeleteService roommateGroupDeleteService;

    @Mock
    private RoommatePostGetService roommatePostGetService;

    @Mock
    private RoommatePostDeleteService roommatePostDeleteService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RoommateGroupUseCase roommateGroupUseCase;

    private static final Long REQUESTER_ID = 1L;

    private User mockUser(Long id, String username, String profileImage) {
        User user = mock(User.class);
        lenient().when(user.getId()).thenReturn(id);
        lenient().when(user.getUsername()).thenReturn(username);
        lenient().when(user.getProfileImage()).thenReturn(profileImage);
        return user;
    }

    private RoommatePost mockPost(Long postId, String title, RoomSize roomSize, Dormitory dormitory, int recruitMemberCount) {
        RoommatePost post = mock(RoommatePost.class);
        lenient().when(post.getId()).thenReturn(postId);
        lenient().when(post.getTitle()).thenReturn(title);
        lenient().when(post.getRoomSize()).thenReturn(roomSize);
        lenient().when(post.getDormitory()).thenReturn(dormitory);
        lenient().when(post.getRecruitMemberCount()).thenReturn(recruitMemberCount);
        return post;
    }

    private RoommateGroup mockGroup(Long groupId, RoommatePost post) {
        RoommateGroup group = mock(RoommateGroup.class);
        lenient().when(group.getId()).thenReturn(groupId);
        lenient().when(group.getPost()).thenReturn(post);
        return group;
    }

    private RoommateGroupMember mockMembership(RoommateGroup group) {
        RoommateGroupMember membership = mock(RoommateGroupMember.class);
        lenient().when(membership.getGroup()).thenReturn(group);
        return membership;
    }

    private RoommateGroupMember mockMember(RoommateGroup group, User user, GroupMemberRole role) {
        RoommateGroupMember member = mock(RoommateGroupMember.class);
        lenient().when(member.getGroup()).thenReturn(group);
        lenient().when(member.getUser()).thenReturn(user);
        lenient().when(member.getRole()).thenReturn(role);
        return member;
    }

    @Nested
    @DisplayName("내가 속한 룸메이트 그룹 목록 조회 시")
    class GetMyRoommateGroups {

        @Test
        @DisplayName("MEMBER로 1개 그룹에 소속되면 해당 그룹 1건과 멤버·역할을 반환한다")
        void MEMBER_단일_그룹_조회() {
            // given
            RoommatePost post = mockPost(10L, "기숙사 룸메 구해요", RoomSize.THREE_PERSON, Dormitory.DORM_1, 3);
            RoommateGroup group = mockGroup(100L, post);
            User leader = mockUser(2L, "리더", "leader.png");
            User me = mockUser(REQUESTER_ID, "나", "me.png");
            RoommateGroupMember membership = mockMembership(group);
            List<RoommateGroupMember> allMembers = List.of(
                    mockMember(group, leader, GroupMemberRole.LEADER),
                    mockMember(group, me, GroupMemberRole.MEMBER));

            given(roommateGroupMemberGetService.getMembershipsByUserId(REQUESTER_ID))
                    .willReturn(List.of(membership));
            given(roommateGroupMemberGetService.getAllByGroupIds(anyList()))
                    .willReturn(allMembers);

            // when
            List<MyRoommateGroupResponse> result = roommateGroupUseCase.getMyRoommateGroups(REQUESTER_ID);

            // then
            assertThat(result).hasSize(1);
            MyRoommateGroupResponse response = result.getFirst();
            assertThat(response.groupId()).isEqualTo(100L);
            assertThat(response.postId()).isEqualTo(10L);
            assertThat(response.postTitle()).isEqualTo("기숙사 룸메 구해요");
            assertThat(response.roomSize()).isEqualTo(RoomSize.THREE_PERSON);
            assertThat(response.dormitory()).isEqualTo(Dormitory.DORM_1);
            assertThat(response.members())
                    .extracting(GroupMemberResponse::userId, GroupMemberResponse::username, m -> m.profileImage(), m -> m.role())
                    .containsExactlyInAnyOrder(
                            Tuple.tuple(2L, "리더", "leader.png", GroupMemberRole.LEADER),
                            Tuple.tuple(1L, "나", "me.png", GroupMemberRole.MEMBER));
        }

        @Test
        @DisplayName("LEADER로 소속된 그룹은 role=LEADER로 반환된다")
        void LEADER_역할_반환() {
            // given
            RoommatePost post = mockPost(11L, "내 모집글", RoomSize.TWO_PERSON, Dormitory.DORM_2, 1);
            RoommateGroup group = mockGroup(101L, post);
            User me = mockUser(REQUESTER_ID, "나", null);
            RoommateGroupMember membership = mockMembership(group);
            List<RoommateGroupMember> allMembers = List.of(mockMember(group, me, GroupMemberRole.LEADER));

            given(roommateGroupMemberGetService.getMembershipsByUserId(REQUESTER_ID))
                    .willReturn(List.of(membership));
            given(roommateGroupMemberGetService.getAllByGroupIds(anyList()))
                    .willReturn(allMembers);

            // when
            List<MyRoommateGroupResponse> result = roommateGroupUseCase.getMyRoommateGroups(REQUESTER_ID);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().members()).hasSize(1);
            assertThat(result.getFirst().members().getFirst().role()).isEqualTo(GroupMemberRole.LEADER);
        }

        @Test
        @DisplayName("LEADER 1개 + MEMBER 1개 겸직 시 그룹 2건을 반환한다")
        void LEADER_MEMBER_겸직_2건_반환() {
            // given
            RoommatePost leaderPost = mockPost(20L, "내가 만든 글", RoomSize.TWO_PERSON, Dormitory.DORM_1, 1);
            RoommateGroup leaderGroup = mockGroup(200L, leaderPost);
            RoommatePost memberPost = mockPost(21L, "합류한 글", RoomSize.FOUR_PERSON, Dormitory.DORM_3, 3);
            RoommateGroup memberGroup = mockGroup(201L, memberPost);
            User me = mockUser(REQUESTER_ID, "나", "me.png");
            RoommateGroupMember leaderMembership = mockMembership(leaderGroup);
            RoommateGroupMember memberMembership = mockMembership(memberGroup);
            List<RoommateGroupMember> allMembers = List.of(
                    mockMember(leaderGroup, me, GroupMemberRole.LEADER),
                    mockMember(memberGroup, me, GroupMemberRole.MEMBER));

            given(roommateGroupMemberGetService.getMembershipsByUserId(REQUESTER_ID))
                    .willReturn(List.of(leaderMembership, memberMembership));
            given(roommateGroupMemberGetService.getAllByGroupIds(anyList()))
                    .willReturn(allMembers);

            // when
            List<MyRoommateGroupResponse> result = roommateGroupUseCase.getMyRoommateGroups(REQUESTER_ID);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(MyRoommateGroupResponse::groupId)
                    .containsExactlyInAnyOrder(200L, 201L);
        }

        @Test
        @DisplayName("총 정원은 recruitMemberCount + 1 (LEADER 포함)로 계산된다")
        void 총_정원_계산() {
            // given
            RoommatePost post = mockPost(30L, "정원 테스트", RoomSize.FOUR_PERSON, Dormitory.DORM_2, 3);
            RoommateGroup group = mockGroup(300L, post);
            User me = mockUser(REQUESTER_ID, "나", null);
            RoommateGroupMember membership = mockMembership(group);
            List<RoommateGroupMember> allMembers = List.of(mockMember(group, me, GroupMemberRole.LEADER));

            given(roommateGroupMemberGetService.getMembershipsByUserId(REQUESTER_ID))
                    .willReturn(List.of(membership));
            given(roommateGroupMemberGetService.getAllByGroupIds(anyList()))
                    .willReturn(allMembers);

            // when
            List<MyRoommateGroupResponse> result = roommateGroupUseCase.getMyRoommateGroups(REQUESTER_ID);

            // then
            assertThat(result.get(0).totalCapacity()).isEqualTo(4);
        }

        @Test
        @DisplayName("현재 인원은 LEADER 포함 전체 멤버 수로 계산된다")
        void 현재_인원_계산() {
            // given
            RoommatePost post = mockPost(31L, "인원 테스트", RoomSize.FOUR_PERSON, Dormitory.DORM_1, 3);
            RoommateGroup group = mockGroup(301L, post);
            User leader = mockUser(2L, "리더", null);
            User member1 = mockUser(3L, "멤버1", null);
            User me = mockUser(REQUESTER_ID, "나", null);
            RoommateGroupMember membership = mockMembership(group);
            List<RoommateGroupMember> allMembers = List.of(
                    mockMember(group, leader, GroupMemberRole.LEADER),
                    mockMember(group, member1, GroupMemberRole.MEMBER),
                    mockMember(group, me, GroupMemberRole.MEMBER));

            given(roommateGroupMemberGetService.getMembershipsByUserId(REQUESTER_ID))
                    .willReturn(List.of(membership));
            given(roommateGroupMemberGetService.getAllByGroupIds(anyList()))
                    .willReturn(allMembers);

            // when
            List<MyRoommateGroupResponse> result = roommateGroupUseCase.getMyRoommateGroups(REQUESTER_ID);

            // then
            assertThat(result.getFirst().currentMemberCount()).isEqualTo(3);
        }

        @Test
        @DisplayName("소속 그룹이 없으면 빈 배열을 반환하고 멤버 조회를 호출하지 않는다")
        void 소속_그룹_없음_빈_배열() {
            // given
            given(roommateGroupMemberGetService.getMembershipsByUserId(REQUESTER_ID))
                    .willReturn(List.of());

            // when
            List<MyRoommateGroupResponse> result = roommateGroupUseCase.getMyRoommateGroups(REQUESTER_ID);

            // then
            assertThat(result).isEmpty();
            then(roommateGroupMemberGetService).should(never()).getAllByGroupIds(anyList());
        }

        @Test
        @DisplayName("profileImage가 null인 멤버도 정상 매핑된다")
        void profileImage_null_매핑() {
            // given
            RoommatePost post = mockPost(40L, "널 테스트", RoomSize.TWO_PERSON, Dormitory.DORM_3, 1);
            RoommateGroup group = mockGroup(400L, post);
            User me = mockUser(REQUESTER_ID, "나", null);
            RoommateGroupMember membership = mockMembership(group);
            List<RoommateGroupMember> allMembers = List.of(mockMember(group, me, GroupMemberRole.LEADER));

            given(roommateGroupMemberGetService.getMembershipsByUserId(REQUESTER_ID))
                    .willReturn(List.of(membership));
            given(roommateGroupMemberGetService.getAllByGroupIds(anyList()))
                    .willReturn(allMembers);

            // when
            List<MyRoommateGroupResponse> result = roommateGroupUseCase.getMyRoommateGroups(REQUESTER_ID);

            // then
            assertThat(result.getFirst().members().getFirst().profileImage()).isNull();
        }

        @Test
        @DisplayName("LEADER만 있는 그룹은 현재 인원 1, 멤버 1건을 반환한다")
        void LEADER만_있는_그룹() {
            // given
            RoommatePost post = mockPost(41L, "리더만", RoomSize.TWO_PERSON, Dormitory.DORM_1, 1);
            RoommateGroup group = mockGroup(401L, post);
            User me = mockUser(REQUESTER_ID, "나", null);
            RoommateGroupMember membership = mockMembership(group);
            List<RoommateGroupMember> allMembers = List.of(mockMember(group, me, GroupMemberRole.LEADER));

            given(roommateGroupMemberGetService.getMembershipsByUserId(REQUESTER_ID))
                    .willReturn(List.of(membership));
            given(roommateGroupMemberGetService.getAllByGroupIds(anyList()))
                    .willReturn(allMembers);

            // when
            List<MyRoommateGroupResponse> result = roommateGroupUseCase.getMyRoommateGroups(REQUESTER_ID);

            // then
            assertThat(result.getFirst().currentMemberCount()).isEqualTo(1);
            assertThat(result.getFirst().members()).hasSize(1);
        }

        @Test
        @DisplayName("정원이 가득 차면 현재 인원이 총 정원과 같다")
        void 정원_가득_찬_그룹() {
            // given
            RoommatePost post = mockPost(42L, "정원 가득", RoomSize.THREE_PERSON, Dormitory.DORM_2, 2);
            RoommateGroup group = mockGroup(402L, post);
            User leader = mockUser(2L, "리더", null);
            User m1 = mockUser(3L, "멤버1", null);
            User me = mockUser(REQUESTER_ID, "나", null);
            RoommateGroupMember membership = mockMembership(group);
            List<RoommateGroupMember> allMembers = List.of(
                    mockMember(group, leader, GroupMemberRole.LEADER),
                    mockMember(group, m1, GroupMemberRole.MEMBER),
                    mockMember(group, me, GroupMemberRole.MEMBER));

            given(roommateGroupMemberGetService.getMembershipsByUserId(REQUESTER_ID))
                    .willReturn(List.of(membership));
            given(roommateGroupMemberGetService.getAllByGroupIds(anyList()))
                    .willReturn(allMembers);

            // when
            List<MyRoommateGroupResponse> result = roommateGroupUseCase.getMyRoommateGroups(REQUESTER_ID);

            // then
            MyRoommateGroupResponse response = result.getFirst();
            assertThat(response.currentMemberCount()).isEqualTo(response.totalCapacity());
        }
    }

    @Nested
    @DisplayName("룸메이트 그룹 탈퇴 시")
    class LeaveRoommateGroup {

        private static final Long GROUP_ID = 500L;

        private RoommateGroupMember mockLeaveMembership(GroupMemberRole role, RoommateGroup group) {
            RoommateGroupMember membership = mock(RoommateGroupMember.class);
            lenient().when(membership.getRole()).thenReturn(role);
            lenient().when(membership.getGroup()).thenReturn(group);
            return membership;
        }

        private RoommatePost realPost() {
            return RoommatePost.create(mock(User.class), "기숙사 룸메 구해요", "함께 지낼 룸메이트를 찾습니다.",
                    RoomSize.TWO_PERSON, 1, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
        }

        @Test
        @DisplayName("MEMBER가 본인 소속 그룹을 탈퇴하면 멤버십이 Soft Delete된다")
        void MEMBER_탈퇴_성공() {
            // given
            RoommatePost post = mock(RoommatePost.class);
            RoommateGroup group = mockGroup(GROUP_ID, post);
            RoommateGroupMember membership = mockLeaveMembership(GroupMemberRole.MEMBER, group);
            given(roommateGroupMemberGetService.getActiveMembership(GROUP_ID, REQUESTER_ID))
                    .willReturn(membership);

            // when
            roommateGroupUseCase.leaveRoommateGroup(REQUESTER_ID, GROUP_ID);

            // then
            then(roommateGroupMemberDeleteService).should().delete(membership);
            then(roommateGroupMemberDeleteService).should(never()).deleteAll(anyList());
            then(roommateGroupDeleteService).should(never()).delete(any());
            then(roommatePostDeleteService).should(never()).deletePost(any(), any());
        }

        @Test
        @DisplayName("MEMBER 탈퇴 시 연결된 모집글이 CLOSED면 OPEN으로 복귀한다")
        void 모집글_CLOSED_OPEN_복귀() {
            // given
            RoommatePost post = realPost();
            post.close();
            RoommateGroup group = mockGroup(GROUP_ID, post);
            RoommateGroupMember membership = mockLeaveMembership(GroupMemberRole.MEMBER, group);
            given(roommateGroupMemberGetService.getActiveMembership(GROUP_ID, REQUESTER_ID))
                    .willReturn(membership);

            // when
            roommateGroupUseCase.leaveRoommateGroup(REQUESTER_ID, GROUP_ID);

            // then
            assertThat(post.isEditable()).isTrue();
        }

        @Test
        @DisplayName("MEMBER 탈퇴 시 연결된 모집글이 OPEN이면 그대로 OPEN을 유지한다 (멱등)")
        void 모집글_OPEN_변경_없음() {
            // given
            RoommatePost post = realPost();
            RoommateGroup group = mockGroup(GROUP_ID, post);
            RoommateGroupMember membership = mockLeaveMembership(GroupMemberRole.MEMBER, group);
            given(roommateGroupMemberGetService.getActiveMembership(GROUP_ID, REQUESTER_ID))
                    .willReturn(membership);

            // when
            roommateGroupUseCase.leaveRoommateGroup(REQUESTER_ID, GROUP_ID);

            // then
            assertThat(post.isEditable()).isTrue();
        }

        @Test
        @DisplayName("그룹이 없거나 요청자가 미소속이면 404 ROOMMATE_GROUP_MEMBERSHIP_NOT_FOUND")
        void 미소속_404() {
            // given
            given(roommateGroupMemberGetService.getActiveMembership(GROUP_ID, REQUESTER_ID))
                    .willThrow(new RoommateGroupMembershipNotFoundException());

            // when & then
            assertThatThrownBy(() -> roommateGroupUseCase.leaveRoommateGroup(REQUESTER_ID, GROUP_ID))
                    .isInstanceOf(RoommateGroupMembershipNotFoundException.class);
            then(roommateGroupMemberDeleteService).should(never()).delete(any());
            then(roommateGroupMemberDeleteService).should(never()).deleteAll(anyList());
            then(roommateGroupDeleteService).should(never()).delete(any());
            then(roommatePostDeleteService).should(never()).deletePost(any(), any());
        }

        @Test
        @DisplayName("이미 탈퇴한 멤버십에 재탈퇴를 요청하면 404 ROOMMATE_GROUP_MEMBERSHIP_NOT_FOUND")
        void 재탈퇴_404() {
            // given
            given(roommateGroupMemberGetService.getActiveMembership(GROUP_ID, REQUESTER_ID))
                    .willThrow(new RoommateGroupMembershipNotFoundException());

            // when & then
            assertThatThrownBy(() -> roommateGroupUseCase.leaveRoommateGroup(REQUESTER_ID, GROUP_ID))
                    .isInstanceOf(RoommateGroupMembershipNotFoundException.class);
            then(roommateGroupMemberDeleteService).should(never()).delete(any());
        }

        @Test
        @DisplayName("LEADER가 탈퇴하면 그룹이 해체되어 모든 멤버십·모집글·공유 라이프스타일·그룹이 Soft Delete된다")
        void LEADER_탈퇴_그룹_해체() {
            // given
            RoommatePost post = mock(RoommatePost.class);
            RoommateGroup group = mockGroup(GROUP_ID, post);
            RoommateGroupMember leaderMembership = mockLeaveMembership(GroupMemberRole.LEADER, group);
            User leader = mockUser(REQUESTER_ID, "리더", null);
            User member1 = mockUser(2L, "멤버1", null);
            List<RoommateGroupMember> activeMembers = List.of(
                    mockMember(group, leader, GroupMemberRole.LEADER),
                    mockMember(group, member1, GroupMemberRole.MEMBER));
            PostSharedLifestyle sharedLifestyle = mock(PostSharedLifestyle.class);

            given(roommateGroupMemberGetService.getActiveMembership(GROUP_ID, REQUESTER_ID))
                    .willReturn(leaderMembership);
            given(roommateGroupMemberGetService.getActiveMembersByGroupId(GROUP_ID))
                    .willReturn(activeMembers);
            given(roommatePostGetService.getSharedLifestyleByPost(post)).willReturn(sharedLifestyle);

            // when
            roommateGroupUseCase.leaveRoommateGroup(REQUESTER_ID, GROUP_ID);

            // then
            then(roommateGroupMemberDeleteService).should().deleteAll(activeMembers);
            then(roommatePostDeleteService).should().deletePost(post, sharedLifestyle);
            then(roommateGroupDeleteService).should().delete(group);
            then(roommateGroupMemberDeleteService).should(never()).delete(any());
            then(post).should(never()).open();
        }

        @Test
        @DisplayName("LEADER 단독 그룹도 정상 해체된다 (멤버십 1건 Soft Delete)")
        void LEADER_단독_해체() {
            // given
            RoommatePost post = mock(RoommatePost.class);
            RoommateGroup group = mockGroup(GROUP_ID, post);
            RoommateGroupMember leaderMembership = mockLeaveMembership(GroupMemberRole.LEADER, group);
            User leader = mockUser(REQUESTER_ID, "리더", null);
            List<RoommateGroupMember> activeMembers = List.of(mockMember(group, leader, GroupMemberRole.LEADER));
            PostSharedLifestyle sharedLifestyle = mock(PostSharedLifestyle.class);

            given(roommateGroupMemberGetService.getActiveMembership(GROUP_ID, REQUESTER_ID))
                    .willReturn(leaderMembership);
            given(roommateGroupMemberGetService.getActiveMembersByGroupId(GROUP_ID))
                    .willReturn(activeMembers);
            given(roommatePostGetService.getSharedLifestyleByPost(post)).willReturn(sharedLifestyle);

            // when
            roommateGroupUseCase.leaveRoommateGroup(REQUESTER_ID, GROUP_ID);

            // then
            then(roommateGroupMemberDeleteService).should().deleteAll(activeMembers);
            then(roommatePostDeleteService).should().deletePost(post, sharedLifestyle);
            then(roommateGroupDeleteService).should().delete(group);
        }

        @Test
        @DisplayName("LEADER·MEMBER 겸직 사용자가 MEMBER 그룹을 탈퇴하면 해당 MEMBER 멤버십만 삭제된다 (LEADER 그룹 영향 없음)")
        void 겸직_MEMBER_그룹만_탈퇴() {
            // given
            RoommatePost post = mock(RoommatePost.class);
            RoommateGroup group = mockGroup(GROUP_ID, post);
            RoommateGroupMember memberMembership = mockLeaveMembership(GroupMemberRole.MEMBER, group);
            given(roommateGroupMemberGetService.getActiveMembership(GROUP_ID, REQUESTER_ID))
                    .willReturn(memberMembership);

            // when
            roommateGroupUseCase.leaveRoommateGroup(REQUESTER_ID, GROUP_ID);

            // then
            then(roommateGroupMemberDeleteService).should().delete(memberMembership);
            then(roommateGroupDeleteService).should(never()).delete(any());
            then(roommatePostDeleteService).should(never()).deletePost(any(), any());
        }

        @Test
        @DisplayName("LEADER 탈퇴로 그룹이 해체되면 방장을 제외한 멤버에게 알림 이벤트가 발행된다")
        void LEADER_탈퇴_시_해체_이벤트_발행() {
            // given
            RoommatePost post = mockPost(60L, "기숙사 룸메 구해요", RoomSize.THREE_PERSON, Dormitory.DORM_1, 2);
            RoommateGroup group = mockGroup(GROUP_ID, post);
            RoommateGroupMember leaderMembership = mockLeaveMembership(GroupMemberRole.LEADER, group);
            User leader = mockUser(REQUESTER_ID, "리더", null);
            User member1 = mockUser(2L, "멤버1", null);
            User member2 = mockUser(3L, "멤버2", null);
            List<RoommateGroupMember> activeMembers = List.of(
                    mockMember(group, leader, GroupMemberRole.LEADER),
                    mockMember(group, member1, GroupMemberRole.MEMBER),
                    mockMember(group, member2, GroupMemberRole.MEMBER));
            PostSharedLifestyle sharedLifestyle = mock(PostSharedLifestyle.class);

            given(roommateGroupMemberGetService.getActiveMembership(GROUP_ID, REQUESTER_ID))
                    .willReturn(leaderMembership);
            given(roommateGroupMemberGetService.getActiveMembersByGroupId(GROUP_ID))
                    .willReturn(activeMembers);
            given(roommatePostGetService.getSharedLifestyleByPost(post)).willReturn(sharedLifestyle);

            // when
            roommateGroupUseCase.leaveRoommateGroup(REQUESTER_ID, GROUP_ID);

            // then
            ArgumentCaptor<RoommateGroupDisbandedEvent> captor =
                    ArgumentCaptor.forClass(RoommateGroupDisbandedEvent.class);
            then(eventPublisher).should().publishEvent(captor.capture());
            RoommateGroupDisbandedEvent published = captor.getValue();
            assertThat(published.leaderId()).isEqualTo(REQUESTER_ID);
            assertThat(published.memberIds()).containsExactlyInAnyOrder(2L, 3L);
            assertThat(published.postTitle()).isEqualTo("기숙사 룸메 구해요");
        }

        @Test
        @DisplayName("LEADER 단독 그룹 해체 시 알릴 멤버가 없으면 이벤트가 발행되지 않는다")
        void LEADER_단독_해체_이벤트_미발행() {
            // given
            RoommatePost post = mockPost(61L, "혼자 모집중", RoomSize.TWO_PERSON, Dormitory.DORM_1, 1);
            RoommateGroup group = mockGroup(GROUP_ID, post);
            RoommateGroupMember leaderMembership = mockLeaveMembership(GroupMemberRole.LEADER, group);
            User leader = mockUser(REQUESTER_ID, "리더", null);
            List<RoommateGroupMember> activeMembers = List.of(mockMember(group, leader, GroupMemberRole.LEADER));
            PostSharedLifestyle sharedLifestyle = mock(PostSharedLifestyle.class);

            given(roommateGroupMemberGetService.getActiveMembership(GROUP_ID, REQUESTER_ID))
                    .willReturn(leaderMembership);
            given(roommateGroupMemberGetService.getActiveMembersByGroupId(GROUP_ID))
                    .willReturn(activeMembers);
            given(roommatePostGetService.getSharedLifestyleByPost(post)).willReturn(sharedLifestyle);

            // when
            roommateGroupUseCase.leaveRoommateGroup(REQUESTER_ID, GROUP_ID);

            // then
            then(eventPublisher).should(never()).publishEvent(any(RoommateGroupDisbandedEvent.class));
        }

        @Test
        @DisplayName("MEMBER 탈퇴 시에는 그룹 해체 이벤트가 발행되지 않는다")
        void MEMBER_탈퇴_시_이벤트_미발행() {
            // given
            RoommatePost post = mock(RoommatePost.class);
            RoommateGroup group = mockGroup(GROUP_ID, post);
            RoommateGroupMember membership = mockLeaveMembership(GroupMemberRole.MEMBER, group);
            given(roommateGroupMemberGetService.getActiveMembership(GROUP_ID, REQUESTER_ID))
                    .willReturn(membership);

            // when
            roommateGroupUseCase.leaveRoommateGroup(REQUESTER_ID, GROUP_ID);

            // then
            then(eventPublisher).should(never()).publishEvent(any(RoommateGroupDisbandedEvent.class));
        }
    }
}
