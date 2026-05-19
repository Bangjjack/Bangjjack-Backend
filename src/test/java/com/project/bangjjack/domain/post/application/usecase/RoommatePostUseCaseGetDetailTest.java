package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.bookmark.domain.service.BookmarkGetService;
import com.project.bangjjack.domain.post.application.dto.response.RoommatePostDetailResponse;
import com.project.bangjjack.domain.post.application.exception.PostNotFoundException;
import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.roommategroup.application.dto.response.GroupMemberResponse;
import com.project.bangjjack.domain.roommategroup.domain.entity.GroupMemberRole;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupMemberGetService;
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

import static com.project.bangjjack.domain.post.application.usecase.RoommatePostFixture.postOwnedBy;
import static com.project.bangjjack.domain.post.application.usecase.RoommatePostFixture.sharedLifestyleFor;
import static com.project.bangjjack.domain.post.application.usecase.RoommatePostFixture.userWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class RoommatePostUseCaseGetDetailTest {

    @Mock
    private RoommatePostGetService roommatePostGetService;

    @Mock
    private BookmarkGetService bookmarkGetService;

    @Mock
    private RoommateGroupMemberGetService roommateGroupMemberGetService;

    @InjectMocks
    private RoommatePostUseCase roommatePostUseCase;

    private static RoommateGroupMember mockMember(User user, GroupMemberRole role) {
        RoommateGroupMember member = mock(RoommateGroupMember.class);
        lenient().when(member.getUser()).thenReturn(user);
        lenient().when(member.getRole()).thenReturn(role);
        return member;
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

            given(roommatePostGetService.getSharedLifestyleWithPostAndUserByPostId(1L)).willReturn(sharedLifestyle);
            given(bookmarkGetService.existsActiveBookmark(userId, 1L)).willReturn(false);
            given(roommateGroupMemberGetService.getActiveMembersWithUserByPostId(1L)).willReturn(members);

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

            given(roommatePostGetService.getSharedLifestyleWithPostAndUserByPostId(1L)).willReturn(sharedLifestyle);
            given(bookmarkGetService.existsActiveBookmark(viewerId, 1L)).willReturn(false);
            given(roommateGroupMemberGetService.getActiveMembersWithUserByPostId(1L)).willReturn(members);

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

            given(roommatePostGetService.getSharedLifestyleWithPostAndUserByPostId(1L)).willReturn(sharedLifestyle);
            given(bookmarkGetService.existsActiveBookmark(userId, 1L)).willReturn(true);

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

            given(roommatePostGetService.getSharedLifestyleWithPostAndUserByPostId(1L)).willReturn(sharedLifestyle);
            given(bookmarkGetService.existsActiveBookmark(userId, 1L)).willReturn(false);

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

            // when
            RoommatePostDetailResponse response = roommatePostUseCase.getPostDetail(ownerId, 1L);

            // then
            assertThat(response.members())
                    .extracting(GroupMemberResponse::userId, GroupMemberResponse::username,
                            GroupMemberResponse::profileImage, GroupMemberResponse::role)
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

            // when
            RoommatePostDetailResponse response = roommatePostUseCase.getPostDetail(ownerId, 1L);

            // then
            assertThat(response.members()).hasSize(1);
            assertThat(response.members().getFirst().role()).isEqualTo(GroupMemberRole.LEADER);
            assertThat(response.members().getFirst().userId()).isEqualTo(ownerId);
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
