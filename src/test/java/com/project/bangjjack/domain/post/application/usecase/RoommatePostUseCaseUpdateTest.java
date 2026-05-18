package com.project.bangjjack.domain.post.application.usecase;

import static com.project.bangjjack.domain.post.application.usecase.RoommatePostFixture.postOwnedBy;
import static com.project.bangjjack.domain.post.application.usecase.RoommatePostFixture.sharedLifestyleFor;
import static com.project.bangjjack.domain.post.application.usecase.RoommatePostFixture.userWithId;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.project.bangjjack.domain.post.application.dto.request.SharedLifestyleRequest;
import com.project.bangjjack.domain.post.application.dto.request.UpdateRoommatePostRequest;
import com.project.bangjjack.domain.post.application.exception.InvalidRecruitMemberCountException;
import com.project.bangjjack.domain.post.application.exception.PostNotEditableException;
import com.project.bangjjack.domain.post.application.exception.PostNotFoundException;
import com.project.bangjjack.domain.post.application.exception.PostUpdateForbiddenException;
import com.project.bangjjack.domain.post.domain.entity.ItemSharing;
import com.project.bangjjack.domain.post.domain.entity.LightsOutTime;
import com.project.bangjjack.domain.post.domain.entity.PhoneCall;
import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.Recycling;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostCreateService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostDeleteService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostUpdateService;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoommatePostUseCaseUpdateTest {

    @Mock
    private UserGetService userGetService;
    @Mock
    private RoommatePostGetService roommatePostGetService;
    @Mock
    private RoommatePostCreateService roommatePostCreateService;
    @Mock
    private RoommatePostDeleteService roommatePostDeleteService;
    @Mock
    private RoommatePostUpdateService roommatePostUpdateService;

    @InjectMocks
    private RoommatePostUseCase roommatePostUseCase;

    private RoommatePost closedPostOwnedBy(User owner) {
        RoommatePost post = postOwnedBy(owner);
        post.close();
        return post;
    }

    private UpdateRoommatePostRequest validUpdateRequest(RoomSize roomSize, int recruitMemberCount) {
        return new UpdateRoommatePostRequest(
                "수정된 제목",
                roomSize,
                recruitMemberCount,
                "수정된 소개글입니다.",
                new SharedLifestyleRequest(
                        false,
                        Recycling.MANAGE_SEPARATELY,
                        PhoneCall.ONLY_IN_ROOM,
                        ItemSharing.USE_SEPARATELY,
                        false,
                        LightsOutTime.BEFORE_23
                )
        );
    }

    @Nested
    @DisplayName("모집글 수정 시")
    class UpdatePost {

        @Test
        @DisplayName("작성자 본인이 OPEN 모집글에 유효한 요청을 보내면 예외 없이 수정된다")
        void 본인_OPEN_모집글_수정_성공() {
            // given
            Long userId = 1L;
            User owner = userWithId(userId);
            RoommatePost post = postOwnedBy(owner);
            PostSharedLifestyle sharedLifestyle = sharedLifestyleFor(post);

            given(roommatePostGetService.getById(1L)).willReturn(post);
            given(roommatePostGetService.getSharedLifestyleByPost(post)).willReturn(sharedLifestyle);

            // when & then
            assertThatCode(() -> roommatePostUseCase.updatePost(userId, 1L, validUpdateRequest(RoomSize.TWO_PERSON, 1)))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("존재하지 않는 postId로 요청하면 PostNotFoundException이 발생한다")
        void 존재하지_않는_모집글_수정_시_예외_발생() {
            // given
            Long nonExistentPostId = 999L;
            given(roommatePostGetService.getById(nonExistentPostId))
                    .willThrow(PostNotFoundException.class);

            // when & then
            assertThatThrownBy(() -> roommatePostUseCase.updatePost(1L, nonExistentPostId,
                    validUpdateRequest(RoomSize.TWO_PERSON, 1)))
                    .isInstanceOf(PostNotFoundException.class);
        }

        @Test
        @DisplayName("타인의 모집글을 수정하려 하면 PostUpdateForbiddenException이 발생한다")
        void 타인_모집글_수정_시_권한_예외_발생() {
            // given
            Long requesterId = 1L;
            Long ownerId = 2L;
            User owner = userWithId(ownerId);
            RoommatePost post = postOwnedBy(owner);

            given(roommatePostGetService.getById(1L)).willReturn(post);

            // when & then
            assertThatThrownBy(
                    () -> roommatePostUseCase.updatePost(requesterId, 1L, validUpdateRequest(RoomSize.TWO_PERSON, 1)))
                    .isInstanceOf(PostUpdateForbiddenException.class);
        }

        @Test
        @DisplayName("CLOSED 상태 모집글을 수정하려 하면 PostNotEditableException이 발생한다")
        void CLOSED_모집글_수정_시_예외_발생() {
            // given
            Long userId = 1L;
            User owner = userWithId(userId);
            RoommatePost closedPost = closedPostOwnedBy(owner);

            given(roommatePostGetService.getById(1L)).willReturn(closedPost);

            // when & then
            assertThatThrownBy(
                    () -> roommatePostUseCase.updatePost(userId, 1L, validUpdateRequest(RoomSize.TWO_PERSON, 1)))
                    .isInstanceOf(PostNotEditableException.class);
        }

        @Test
        @DisplayName("TWO_PERSON인데 recruitMemberCount=2로 요청하면 InvalidRecruitMemberCountException이 발생한다")
        void 방_유형과_모집인원_불일치_시_예외_발생() {
            // given
            Long userId = 1L;
            User owner = userWithId(userId);
            RoommatePost post = postOwnedBy(owner);

            given(roommatePostGetService.getById(1L)).willReturn(post);

            // when & then
            assertThatThrownBy(
                    () -> roommatePostUseCase.updatePost(userId, 1L, validUpdateRequest(RoomSize.TWO_PERSON, 2)))
                    .isInstanceOf(InvalidRecruitMemberCountException.class);
        }
    }
}
