package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.post.application.dto.response.RoommatePostSummaryResponse;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostCreateService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostDeleteService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.Gender;
import com.project.bangjjack.domain.user.domain.entity.Semester;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import com.project.bangjjack.global.common.response.SliceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class RoommatePostUseCaseGetListTest {

    @Mock
    private UserGetService userGetService;

    @Mock
    private RoommatePostGetService roommatePostGetService;

    @Mock
    private RoommatePostCreateService roommatePostCreateService;

    @Mock
    private RoommatePostDeleteService roommatePostDeleteService;

    @InjectMocks
    private RoommatePostUseCase roommatePostUseCase;

    private User defaultUser() {
        User user = User.create("provider-1", "테스트유저", "test@gachon.ac.kr", null);
        user.completeOnboarding(2000, 2, Gender.MALE, Campus.GLOBAL_CAMPUS, null, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
        return user;
    }

    private RoommatePost post(User user, RoomSize roomSize) {
        return RoommatePost.create(user, "룸메이트 구해요", "같이 지낼 룸메이트 찾습니다.", roomSize, 1,
                Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
    }

    @Nested
    @DisplayName("모집글 목록 조회 시")
    class GetPostList {

        @Test
        @DisplayName("필터 없이 조회하면 서비스에 null 필터를 그대로 전달하고 결과를 반환한다")
        void 필터_없이_조회_시_결과_반환() {
            // given
            PageRequest pageable = PageRequest.of(0, 20);
            User user = defaultUser();
            List<RoommatePost> posts = List.of(post(user, RoomSize.TWO_PERSON));
            given(roommatePostGetService.getPostList(null, null, pageable))
                    .willReturn(new SliceImpl<>(posts, pageable, false));

            // when
            SliceResponse<RoommatePostSummaryResponse> result = roommatePostUseCase.getPostList(null, null, pageable);

            // then
            assertThat(result.content()).hasSize(1);
            assertThat(result.hasNext()).isFalse();
            then(roommatePostGetService).should().getPostList(null, null, pageable);
        }

        @Test
        @DisplayName("campus 필터를 전달하면 서비스에 campus가 전달된다")
        void campus_필터_적용_시_서비스에_campus_전달() {
            // given
            PageRequest pageable = PageRequest.of(0, 20);
            given(roommatePostGetService.getPostList(Campus.GLOBAL_CAMPUS, null, pageable))
                    .willReturn(new SliceImpl<>(List.of(), pageable, false));

            // when
            roommatePostUseCase.getPostList(Campus.GLOBAL_CAMPUS, null, pageable);

            // then
            then(roommatePostGetService).should().getPostList(Campus.GLOBAL_CAMPUS, null, pageable);
        }

        @Test
        @DisplayName("roomSize 필터를 전달하면 서비스에 roomSize가 전달된다")
        void roomSize_필터_적용_시_서비스에_roomSize_전달() {
            // given
            PageRequest pageable = PageRequest.of(0, 20);
            given(roommatePostGetService.getPostList(null, RoomSize.TWO_PERSON, pageable))
                    .willReturn(new SliceImpl<>(List.of(), pageable, false));

            // when
            roommatePostUseCase.getPostList(null, RoomSize.TWO_PERSON, pageable);

            // then
            then(roommatePostGetService).should().getPostList(null, RoomSize.TWO_PERSON, pageable);
        }

        @Test
        @DisplayName("다음 페이지가 존재하면 hasNext=true를 반환한다")
        void 다음_페이지_존재_시_hasNext_true() {
            // given
            PageRequest pageable = PageRequest.of(0, 2);
            User user = defaultUser();
            List<RoommatePost> posts = List.of(
                    post(user, RoomSize.TWO_PERSON),
                    post(user, RoomSize.THREE_PERSON)
            );
            given(roommatePostGetService.getPostList(null, null, pageable))
                    .willReturn(new SliceImpl<>(posts, pageable, true));

            // when
            SliceResponse<RoommatePostSummaryResponse> result = roommatePostUseCase.getPostList(null, null, pageable);

            // then
            assertThat(result.hasNext()).isTrue();
            assertThat(result.content()).hasSize(2);
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록과 hasNext=false를 반환한다")
        void 결과_없으면_빈_목록_반환() {
            // given
            PageRequest pageable = PageRequest.of(0, 20);
            given(roommatePostGetService.getPostList(null, null, pageable))
                    .willReturn(new SliceImpl<>(List.of(), pageable, false));

            // when
            SliceResponse<RoommatePostSummaryResponse> result = roommatePostUseCase.getPostList(null, null, pageable);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.hasNext()).isFalse();
        }

        @Test
        @DisplayName("campus와 roomSize를 동시에 전달하면 두 필터가 모두 서비스에 전달된다")
        void campus와_roomSize_동시_필터_시_서비스에_모두_전달() {
            // given
            PageRequest pageable = PageRequest.of(0, 20);
            given(roommatePostGetService.getPostList(Campus.GLOBAL_CAMPUS, RoomSize.TWO_PERSON, pageable))
                    .willReturn(new SliceImpl<>(List.of(), pageable, false));

            // when
            roommatePostUseCase.getPostList(Campus.GLOBAL_CAMPUS, RoomSize.TWO_PERSON, pageable);

            // then
            then(roommatePostGetService).should().getPostList(Campus.GLOBAL_CAMPUS, RoomSize.TWO_PERSON, pageable);
        }

        @Test
        @DisplayName("반환된 게시글의 description 필드가 올바르게 매핑된다")
        void 반환_게시글의_description_필드_매핑_검증() {
            // given
            PageRequest pageable = PageRequest.of(0, 20);
            User user = defaultUser();
            RoommatePost roommatePost = post(user, RoomSize.TWO_PERSON);
            given(roommatePostGetService.getPostList(null, null, pageable))
                    .willReturn(new SliceImpl<>(List.of(roommatePost), pageable, false));

            // when
            SliceResponse<RoommatePostSummaryResponse> result = roommatePostUseCase.getPostList(null, null, pageable);

            // then
            assertThat(result.content().get(0).description()).isEqualTo("같이 지낼 룸메이트 찾습니다.");
        }

        @Test
        @DisplayName("기숙사가 매핑되지 않은 캠퍼스 필터 시 빈 목록을 반환한다")
        void 미매핑_캠퍼스_필터_시_빈_목록_반환() {
            // given
            PageRequest pageable = PageRequest.of(0, 20);
            given(roommatePostGetService.getPostList(Campus.MEDICAL_CAMPUS, null, pageable))
                    .willReturn(new SliceImpl<>(List.of(), pageable, false));

            // when
            SliceResponse<RoommatePostSummaryResponse> result = roommatePostUseCase.getPostList(Campus.MEDICAL_CAMPUS, null, pageable);

            // then
            assertThat(result.content()).isEmpty();
            assertThat(result.hasNext()).isFalse();
        }
    }
}
