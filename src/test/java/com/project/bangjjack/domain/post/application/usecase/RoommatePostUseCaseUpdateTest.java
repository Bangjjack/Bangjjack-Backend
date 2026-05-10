package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.post.application.dto.request.UpdateRoommatePostRequest;
import com.project.bangjjack.domain.post.application.dto.request.UpdateSharedLifestyleRequest;
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
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostUpdateService;
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

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RoommatePostUseCaseUpdateTest {

    @Mock
    private RoommatePostGetService roommatePostGetService;

    @Mock
    private RoommatePostUpdateService roommatePostUpdateService;

    @InjectMocks
    private RoommatePostUseCase roommatePostUseCase;

    private User userWithId(Long id) throws Exception {
        User user = User.create("provider-" + id, "테스트유저" + id, "test" + id + "@gachon.ac.kr", null);
        user.completeOnboarding(2000, 2, Gender.MALE, Campus.GLOBAL_CAMPUS, null, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
        Field idField = user.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, id);
        return user;
    }

    private RoommatePost openPostOwnedBy(User owner) {
        return RoommatePost.create(
                owner,
                "룸메이트 구해요",
                "함께 지낼 룸메이트를 찾습니다.",
                RoomSize.TWO_PERSON,
                1,
                Semester.SIXTEEN_WEEKS,
                Dormitory.DORM_1
        );
    }

    private RoommatePost closedPostOwnedBy(User owner) {
        RoommatePost post = openPostOwnedBy(owner);
        post.close();
        return post;
    }

    private PostSharedLifestyle sharedLifestyleFor(RoommatePost post) {
        return PostSharedLifestyle.create(
                post, true, Recycling.SHARE_BIN, PhoneCall.SHORT_CALLS_OKAY,
                ItemSharing.NO_PREFERENCE, true, LightsOutTime.BETWEEN_23_24
        );
    }

    private UpdateRoommatePostRequest validUpdateRequest(RoomSize roomSize, int recruitMemberCount) {
        return new UpdateRoommatePostRequest(
                "수정된 제목",
                roomSize,
                recruitMemberCount,
                "수정된 소개글입니다.",
                new UpdateSharedLifestyleRequest(
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
        void 본인_OPEN_모집글_수정_성공() throws Exception {
            // given
            Long userId = 1L;
            User owner = userWithId(userId);
            RoommatePost post = openPostOwnedBy(owner);
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
            assertThatThrownBy(() -> roommatePostUseCase.updatePost(1L, nonExistentPostId, validUpdateRequest(RoomSize.TWO_PERSON, 1)))
                    .isInstanceOf(PostNotFoundException.class);
        }

        @Test
        @DisplayName("타인의 모집글을 수정하려 하면 PostUpdateForbiddenException이 발생한다")
        void 타인_모집글_수정_시_권한_예외_발생() throws Exception {
            // given
            Long requesterId = 1L;
            Long ownerId = 2L;
            User owner = userWithId(ownerId);
            RoommatePost post = openPostOwnedBy(owner);

            given(roommatePostGetService.getById(1L)).willReturn(post);

            // when & then
            assertThatThrownBy(() -> roommatePostUseCase.updatePost(requesterId, 1L, validUpdateRequest(RoomSize.TWO_PERSON, 1)))
                    .isInstanceOf(PostUpdateForbiddenException.class);
        }

        @Test
        @DisplayName("CLOSED 상태 모집글을 수정하려 하면 PostNotEditableException이 발생한다")
        void CLOSED_모집글_수정_시_예외_발생() throws Exception {
            // given
            Long userId = 1L;
            User owner = userWithId(userId);
            RoommatePost closedPost = closedPostOwnedBy(owner);

            given(roommatePostGetService.getById(1L)).willReturn(closedPost);

            // when & then
            assertThatThrownBy(() -> roommatePostUseCase.updatePost(userId, 1L, validUpdateRequest(RoomSize.TWO_PERSON, 1)))
                    .isInstanceOf(PostNotEditableException.class);
        }

        @Test
        @DisplayName("TWO_PERSON인데 recruitMemberCount=2로 요청하면 InvalidRecruitMemberCountException이 발생한다")
        void 방_유형과_모집인원_불일치_시_예외_발생() throws Exception {
            // given
            Long userId = 1L;
            User owner = userWithId(userId);
            RoommatePost post = openPostOwnedBy(owner);

            given(roommatePostGetService.getById(1L)).willReturn(post);

            // when & then
            assertThatThrownBy(() -> roommatePostUseCase.updatePost(userId, 1L, validUpdateRequest(RoomSize.TWO_PERSON, 2)))
                    .isInstanceOf(InvalidRecruitMemberCountException.class);
        }
    }
}
