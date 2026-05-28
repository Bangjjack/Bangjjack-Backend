package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.post.application.dto.response.MyRoommatePostResponse;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.roommategroup.domain.service.RoommateGroupMemberGetService;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class RoommatePostUseCaseGetMyPostsTest {

    @Mock
    private RoommatePostGetService roommatePostGetService;

    @Mock
    private RoommateGroupMemberGetService roommateGroupMemberGetService;

    @InjectMocks
    private RoommatePostUseCase roommatePostUseCase;

    private User user(Long id) {
        User user = User.create("provider-" + id, "테스트유저" + id, "test" + id + "@gachon.ac.kr", null);
        user.completeOnboarding(2000, 2, Gender.MALE, Campus.GLOBAL_CAMPUS, null, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private RoommatePost openPost(Long id, User owner, RoomSize roomSize, int recruitMemberCount) {
        RoommatePost post = RoommatePost.create(owner, "룸메 구해요 " + id, "함께 지낼 룸메이트를 찾습니다.",
                roomSize, recruitMemberCount, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
        ReflectionTestUtils.setField(post, "id", id);
        return post;
    }

    private RoommatePost closedPost(Long id, User owner, RoomSize roomSize, int recruitMemberCount) {
        RoommatePost post = openPost(id, owner, roomSize, recruitMemberCount);
        post.close();
        return post;
    }

    @Nested
    @DisplayName("내가 쓴 모집글 목록 조회 시")
    class GetMyPosts {

        @Test
        @DisplayName("내가 작성한 OPEN/CLOSED 글을 최신순으로 모두 반환한다")
        void OPEN_CLOSED_모두_반환() {
            // given
            Long userId = 10L;
            User owner = user(userId);
            RoommatePost openPost = openPost(1L, owner, RoomSize.TWO_PERSON, 1);
            RoommatePost closedPost = closedPost(2L, owner, RoomSize.THREE_PERSON, 2);
            given(roommatePostGetService.getPostsByUserId(userId)).willReturn(List.of(openPost, closedPost));
            given(roommateGroupMemberGetService.countActiveByPostIdIn(List.of(1L, 2L)))
                    .willReturn(Map.of(1L, 1L, 2L, 3L));

            // when
            List<MyRoommatePostResponse> result = roommatePostUseCase.getMyPosts(userId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).postId()).isEqualTo(1L);
            assertThat(result.get(0).isClosed()).isFalse();
            assertThat(result.get(1).postId()).isEqualTo(2L);
            assertThat(result.get(1).isClosed()).isTrue();
        }

        @Test
        @DisplayName("totalMemberCount는 recruitMemberCount + 1로 매핑된다")
        void totalMemberCount_매핑() {
            // given
            Long userId = 10L;
            User owner = user(userId);
            RoommatePost post = openPost(1L, owner, RoomSize.FOUR_PERSON, 3);
            given(roommatePostGetService.getPostsByUserId(userId)).willReturn(List.of(post));
            given(roommateGroupMemberGetService.countActiveByPostIdIn(List.of(1L)))
                    .willReturn(Map.of(1L, 1L));

            // when
            List<MyRoommatePostResponse> result = roommatePostUseCase.getMyPosts(userId);

            // then
            assertThat(result.get(0).totalMemberCount()).isEqualTo(4);
        }

        @Test
        @DisplayName("currentMemberCount는 활성 그룹 멤버 수와 일치한다")
        void currentMemberCount_매핑() {
            // given
            Long userId = 10L;
            User owner = user(userId);
            RoommatePost postA = openPost(1L, owner, RoomSize.THREE_PERSON, 2);
            RoommatePost postB = openPost(2L, owner, RoomSize.FOUR_PERSON, 3);
            given(roommatePostGetService.getPostsByUserId(userId)).willReturn(List.of(postA, postB));
            given(roommateGroupMemberGetService.countActiveByPostIdIn(List.of(1L, 2L)))
                    .willReturn(Map.of(1L, 1L, 2L, 4L));

            // when
            List<MyRoommatePostResponse> result = roommatePostUseCase.getMyPosts(userId);

            // then
            assertThat(result.get(0).currentMemberCount()).isEqualTo(1);
            assertThat(result.get(1).currentMemberCount()).isEqualTo(4);
        }

        @Test
        @DisplayName("응답 필드들이 RoommatePost 값과 정확히 매핑된다")
        void 응답_필드_매핑() {
            // given
            Long userId = 10L;
            User owner = user(userId);
            RoommatePost post = openPost(1L, owner, RoomSize.TWO_PERSON, 1);
            given(roommatePostGetService.getPostsByUserId(userId)).willReturn(List.of(post));
            given(roommateGroupMemberGetService.countActiveByPostIdIn(List.of(1L)))
                    .willReturn(Map.of(1L, 1L));

            // when
            MyRoommatePostResponse response = roommatePostUseCase.getMyPosts(userId).get(0);

            // then
            assertThat(response.postId()).isEqualTo(1L);
            assertThat(response.title()).isEqualTo("룸메 구해요 1");
            assertThat(response.description()).isEqualTo("함께 지낼 룸메이트를 찾습니다.");
            assertThat(response.dormitory()).isEqualTo(Dormitory.DORM_1);
            assertThat(response.roomSize()).isEqualTo(RoomSize.TWO_PERSON);
            assertThat(response.totalMemberCount()).isEqualTo(2);
            assertThat(response.currentMemberCount()).isEqualTo(1);
            assertThat(response.isClosed()).isFalse();
        }

        @Test
        @DisplayName("작성한 글이 없으면 빈 배열을 반환하고 멤버 수 집계 쿼리는 호출되지 않는다")
        void 작성_글_없으면_빈_배열_반환() {
            // given
            Long userId = 10L;
            given(roommatePostGetService.getPostsByUserId(userId)).willReturn(List.of());

            // when
            List<MyRoommatePostResponse> result = roommatePostUseCase.getMyPosts(userId);

            // then
            assertThat(result).isEmpty();
            then(roommateGroupMemberGetService).should(never()).countActiveByPostIdIn(org.mockito.ArgumentMatchers.anyList());
        }

        @Test
        @DisplayName("멤버 수 집계 결과에 postId가 없으면 currentMemberCount=0으로 반환한다")
        void 멤버_수_집계_누락_시_0_반환() {
            // given
            Long userId = 10L;
            User owner = user(userId);
            RoommatePost post = openPost(1L, owner, RoomSize.TWO_PERSON, 1);
            given(roommatePostGetService.getPostsByUserId(userId)).willReturn(List.of(post));
            given(roommateGroupMemberGetService.countActiveByPostIdIn(List.of(1L))).willReturn(Map.of());

            // when
            List<MyRoommatePostResponse> result = roommatePostUseCase.getMyPosts(userId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).currentMemberCount()).isEqualTo(0);
        }
    }
}
