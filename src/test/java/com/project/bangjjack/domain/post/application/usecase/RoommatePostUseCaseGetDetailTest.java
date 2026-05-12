package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.post.application.dto.response.RoommatePostDetailResponse;
import com.project.bangjjack.domain.post.application.exception.PostNotFoundException;
import com.project.bangjjack.domain.post.domain.entity.ItemSharing;
import com.project.bangjjack.domain.post.domain.entity.LightsOutTime;
import com.project.bangjjack.domain.post.domain.entity.PhoneCall;
import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.Recycling;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RoommatePostUseCaseGetDetailTest {

    @Mock
    private RoommatePostGetService roommatePostGetService;

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

    private RoommatePost postOwnedBy(User owner) {
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

    private PostSharedLifestyle sharedLifestyleFor(RoommatePost post) {
        return PostSharedLifestyle.create(
                post, true, Recycling.SHARE_BIN, PhoneCall.SHORT_CALLS_OKAY,
                ItemSharing.NO_PREFERENCE, true, LightsOutTime.BETWEEN_23_24
        );
    }

    @Nested
    @DisplayName("모집글 단건 조회 시")
    class GetPostDetail {

        @Test
        @DisplayName("작성자 본인이 조회하면 isOwner=true인 응답을 반환한다")
        void 작성자_본인_조회_시_isOwner_true() throws Exception {
            // given
            Long userId = 1L;
            User owner = userWithId(userId);
            RoommatePost post = postOwnedBy(owner);
            PostSharedLifestyle sharedLifestyle = sharedLifestyleFor(post);

            given(roommatePostGetService.getSharedLifestyleWithPostAndUserByPostId(1L)).willReturn(sharedLifestyle);

            // when
            RoommatePostDetailResponse response = roommatePostUseCase.getPostDetail(userId, 1L);

            // then
            assertThat(response.isOwner()).isTrue();
            assertThat(response.title()).isEqualTo("룸메이트 구해요");
            assertThat(response.author().authorId()).isEqualTo(userId);
        }

        @Test
        @DisplayName("타인이 조회하면 isOwner=false인 응답을 반환한다")
        void 타인_조회_시_isOwner_false() throws Exception {
            // given
            Long ownerId = 1L;
            Long viewerId = 2L;
            User owner = userWithId(ownerId);
            RoommatePost post = postOwnedBy(owner);
            PostSharedLifestyle sharedLifestyle = sharedLifestyleFor(post);

            given(roommatePostGetService.getSharedLifestyleWithPostAndUserByPostId(1L)).willReturn(sharedLifestyle);

            // when
            RoommatePostDetailResponse response = roommatePostUseCase.getPostDetail(viewerId, 1L);

            // then
            assertThat(response.isOwner()).isFalse();
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
