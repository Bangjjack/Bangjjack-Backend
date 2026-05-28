package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.post.application.exception.PostDeleteForbiddenException;
import com.project.bangjjack.domain.post.application.exception.PostNotFoundException;
import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostDeleteService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.roommategroup.application.event.RoommateGroupDisbandedEvent;
import com.project.bangjjack.domain.roommategroup.domain.entity.GroupMemberRole;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroup;
import com.project.bangjjack.domain.roommategroup.domain.entity.RoommateGroupMember;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupDeleteService;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupGetService;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupMemberDeleteService;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupMemberGetService;
import com.project.bangjjack.domain.user.domain.entity.User;
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

import static com.project.bangjjack.domain.post.application.usecase.RoommatePostFixture.postOwnedBy;
import static com.project.bangjjack.domain.post.application.usecase.RoommatePostFixture.sharedLifestyleFor;
import static com.project.bangjjack.domain.post.application.usecase.RoommatePostFixture.userWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class RoommatePostUseCaseDeleteTest {

    @Mock
    private RoommatePostGetService roommatePostGetService;

    @Mock
    private RoommatePostDeleteService roommatePostDeleteService;

    @Mock
    private RoommateGroupGetService roommateGroupGetService;

    @Mock
    private RoommateGroupDeleteService roommateGroupDeleteService;

    @Mock
    private RoommateGroupMemberGetService roommateGroupMemberGetService;

    @Mock
    private RoommateGroupMemberDeleteService roommateGroupMemberDeleteService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RoommatePostUseCase roommatePostUseCase;

    private RoommateGroupMember mockMember(Long userId, GroupMemberRole role) {
        User user = mock(User.class);
        lenient().when(user.getId()).thenReturn(userId);
        RoommateGroupMember member = mock(RoommateGroupMember.class);
        lenient().when(member.getUser()).thenReturn(user);
        lenient().when(member.getRole()).thenReturn(role);
        return member;
    }

    @Nested
    @DisplayName("모집글 삭제 시")
    class DeletePost {

        private static final Long POST_ID = 1L;
        private static final Long GROUP_ID = 100L;

        @Test
        @DisplayName("작성자 본인이 요청하면 모집글·공유생활정보·그룹·모든 활성 멤버십이 함께 Soft Delete된다")
        void 본인_모집글_삭제_시_그룹과_멤버십까지_cascade_삭제() {
            // given
            Long userId = 1L;
            User owner = userWithId(userId);
            RoommatePost post = postOwnedBy(owner);
            PostSharedLifestyle sharedLifestyle = sharedLifestyleFor(post);
            RoommateGroup group = mock(RoommateGroup.class);
            lenient().when(group.getId()).thenReturn(GROUP_ID);
            List<RoommateGroupMember> members = List.of(mockMember(userId, GroupMemberRole.LEADER));

            given(roommatePostGetService.getById(POST_ID)).willReturn(post);
            given(roommateGroupGetService.getByPostId(POST_ID)).willReturn(group);
            given(roommateGroupMemberGetService.getActiveMembersWithUserByPostId(POST_ID)).willReturn(members);
            given(roommatePostGetService.getSharedLifestyleByPost(post)).willReturn(sharedLifestyle);

            // when & then
            assertThatCode(() -> roommatePostUseCase.deletePost(userId, POST_ID))
                    .doesNotThrowAnyException();

            then(roommateGroupMemberDeleteService).should().deleteAll(members);
            then(roommatePostDeleteService).should().deletePost(post, sharedLifestyle);
            then(roommateGroupDeleteService).should().delete(group);
        }

        @Test
        @DisplayName("그룹에 본인 외 다른 멤버가 있으면 그룹 해체 알림 이벤트가 발행된다")
        void 다른_멤버_있을_때_해체_이벤트_발행() {
            // given
            Long leaderId = 1L;
            User owner = userWithId(leaderId);
            RoommatePost post = postOwnedBy(owner);
            PostSharedLifestyle sharedLifestyle = sharedLifestyleFor(post);
            RoommateGroup group = mock(RoommateGroup.class);
            lenient().when(group.getId()).thenReturn(GROUP_ID);
            List<RoommateGroupMember> members = List.of(
                    mockMember(leaderId, GroupMemberRole.LEADER),
                    mockMember(2L, GroupMemberRole.MEMBER),
                    mockMember(3L, GroupMemberRole.MEMBER));

            given(roommatePostGetService.getById(POST_ID)).willReturn(post);
            given(roommateGroupGetService.getByPostId(POST_ID)).willReturn(group);
            given(roommateGroupMemberGetService.getActiveMembersWithUserByPostId(POST_ID)).willReturn(members);
            given(roommatePostGetService.getSharedLifestyleByPost(post)).willReturn(sharedLifestyle);

            // when
            roommatePostUseCase.deletePost(leaderId, POST_ID);

            // then
            ArgumentCaptor<RoommateGroupDisbandedEvent> captor =
                    ArgumentCaptor.forClass(RoommateGroupDisbandedEvent.class);
            then(eventPublisher).should().publishEvent(captor.capture());
            RoommateGroupDisbandedEvent published = captor.getValue();
            assertThat(published.leaderId()).isEqualTo(leaderId);
            assertThat(published.memberIds()).containsExactlyInAnyOrder(2L, 3L);
            assertThat(published.postTitle()).isEqualTo(post.getTitle());
        }

        @Test
        @DisplayName("LEADER 단독 그룹(본인뿐)이면 알림 이벤트는 발행되지 않는다")
        void 단독_그룹_삭제_시_이벤트_미발행() {
            // given
            Long leaderId = 1L;
            User owner = userWithId(leaderId);
            RoommatePost post = postOwnedBy(owner);
            PostSharedLifestyle sharedLifestyle = sharedLifestyleFor(post);
            RoommateGroup group = mock(RoommateGroup.class);
            lenient().when(group.getId()).thenReturn(GROUP_ID);
            List<RoommateGroupMember> members = List.of(mockMember(leaderId, GroupMemberRole.LEADER));

            given(roommatePostGetService.getById(POST_ID)).willReturn(post);
            given(roommateGroupGetService.getByPostId(POST_ID)).willReturn(group);
            given(roommateGroupMemberGetService.getActiveMembersWithUserByPostId(POST_ID)).willReturn(members);
            given(roommatePostGetService.getSharedLifestyleByPost(post)).willReturn(sharedLifestyle);

            // when
            roommatePostUseCase.deletePost(leaderId, POST_ID);

            // then
            then(eventPublisher).should(never()).publishEvent(any(RoommateGroupDisbandedEvent.class));
        }

        @Test
        @DisplayName("존재하지 않는 postId로 요청하면 PostNotFoundException이 발생한다")
        void 존재하지_않는_모집글_삭제_시_예외_발생() {
            // given
            Long nonExistentPostId = 999L;
            given(roommatePostGetService.getById(nonExistentPostId))
                    .willThrow(PostNotFoundException.class);

            // when & then
            assertThatThrownBy(() -> roommatePostUseCase.deletePost(1L, nonExistentPostId))
                    .isInstanceOf(PostNotFoundException.class);

            then(roommatePostDeleteService).should(never()).deletePost(any(), any());
            then(roommateGroupDeleteService).should(never()).delete(any());
            then(roommateGroupMemberDeleteService).should(never()).deleteAll(anyList());
        }

        @Test
        @DisplayName("타인의 모집글을 삭제하려 하면 PostDeleteForbiddenException이 발생한다")
        void 타인_모집글_삭제_시_권한_예외_발생() {
            // given
            Long requesterId = 1L;
            Long ownerId = 2L;
            User owner = userWithId(ownerId);
            RoommatePost post = postOwnedBy(owner);

            given(roommatePostGetService.getById(POST_ID)).willReturn(post);

            // when & then
            assertThatThrownBy(() -> roommatePostUseCase.deletePost(requesterId, POST_ID))
                    .isInstanceOf(PostDeleteForbiddenException.class);

            then(roommatePostDeleteService).should(never()).deletePost(any(), any());
            then(roommateGroupDeleteService).should(never()).delete(any());
            then(roommateGroupMemberDeleteService).should(never()).deleteAll(anyList());
        }
    }
}
