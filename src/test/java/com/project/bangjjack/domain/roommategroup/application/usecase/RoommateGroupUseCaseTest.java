package com.project.bangjjack.domain.roommategroup.application.usecase;

import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.roommategroup.application.dto.response.GroupMemberResponse;
import com.project.bangjjack.domain.roommategroup.application.dto.response.MyRoommateGroupResponse;
import com.project.bangjjack.domain.roommategroup.domain.entity.GroupMemberRole;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroup;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupMemberGetService;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.User;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
}
