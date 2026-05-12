package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.post.application.dto.request.CreateRoommatePostRequest;
import com.project.bangjjack.domain.post.application.dto.request.SharedLifestyleRequest;
import com.project.bangjjack.domain.post.application.exception.AlreadyOpenPostExistsException;
import com.project.bangjjack.domain.post.application.exception.InvalidRecruitMemberCountException;
import com.project.bangjjack.domain.post.application.exception.PostWritePreconditionNotMetException;
import com.project.bangjjack.domain.post.domain.entity.ItemSharing;
import com.project.bangjjack.domain.post.domain.entity.LightsOutTime;
import com.project.bangjjack.domain.post.domain.entity.PhoneCall;
import com.project.bangjjack.domain.post.domain.entity.Recycling;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.domain.service.RoommatePostCreateService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.domain.user.domain.entity.Dormitory;
import com.project.bangjjack.domain.user.domain.entity.Gender;
import com.project.bangjjack.domain.user.domain.entity.Semester;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class RoommatePostUseCaseCreateTest {

    @Mock
    private UserGetService userGetService;

    @Mock
    private RoommatePostGetService roommatePostGetService;

    @Mock
    private RoommatePostCreateService roommatePostCreateService;

    @InjectMocks
    private RoommatePostUseCase roommatePostUseCase;

    private User fullyRegisteredUser() {
        User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
        user.completeOnboarding(2000, 2, Gender.MALE, Campus.GLOBAL_CAMPUS, null, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
        user.completeChecklistRegistration();
        user.completeRoommatePreferenceRegistration();
        return user;
    }

    private CreateRoommatePostRequest validRequest(RoomSize roomSize, int recruitMemberCount) {
        return new CreateRoommatePostRequest(
                "룸메이트 구해요",
                roomSize,
                recruitMemberCount,
                "같이 잘 지낼 룸메이트를 찾습니다.",
                new SharedLifestyleRequest(
                        true,
                        Recycling.SHARE_BIN,
                        PhoneCall.SHORT_CALLS_OKAY,
                        ItemSharing.NO_PREFERENCE,
                        true,
                        LightsOutTime.BETWEEN_23_24
                )
        );
    }

    @Nested
    @DisplayName("모집글 작성 시")
    class CreatePost {

        @Test
        @DisplayName("모든 사전 조건이 충족된 사용자가 유효한 요청을 보내면 예외 없이 모집글이 생성된다")
        void 유효한_요청으로_모집글_생성_성공() {
            // given
            Long userId = 1L;
            User user = fullyRegisteredUser();
            given(userGetService.getById(userId)).willReturn(user);
            given(roommatePostGetService.existsOpenPostByUser(user)).willReturn(false);

            // when & then
            assertThatCode(() -> roommatePostUseCase.createPost(userId, validRequest(RoomSize.TWO_PERSON, 1)))
                    .doesNotThrowAnyException();

            then(roommatePostCreateService).should().createPost(any(), any());
        }

        @Test
        @DisplayName("온보딩 미완료 사용자가 요청하면 PostWritePreconditionNotMetException이 발생한다")
        void 온보딩_미완료_사용자_예외_발생() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            given(userGetService.getById(userId)).willReturn(user);

            // when & then
            assertThatThrownBy(() -> roommatePostUseCase.createPost(userId, validRequest(RoomSize.TWO_PERSON, 1)))
                    .isInstanceOf(PostWritePreconditionNotMetException.class);

            then(roommatePostCreateService).should(never()).createPost(any(), any());
        }

        @Test
        @DisplayName("체크리스트 미등록 사용자가 요청하면 PostWritePreconditionNotMetException이 발생한다")
        void 체크리스트_미등록_사용자_예외_발생() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            user.completeOnboarding(2000, 2, Gender.MALE, Campus.GLOBAL_CAMPUS, null, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
            given(userGetService.getById(userId)).willReturn(user);

            // when & then
            assertThatThrownBy(() -> roommatePostUseCase.createPost(userId, validRequest(RoomSize.TWO_PERSON, 1)))
                    .isInstanceOf(PostWritePreconditionNotMetException.class);
        }

        @Test
        @DisplayName("선호도 미등록 사용자가 요청하면 PostWritePreconditionNotMetException이 발생한다")
        void 선호도_미등록_사용자_예외_발생() {
            // given
            Long userId = 1L;
            User user = User.create("provider-123", "테스트유저", "test@gachon.ac.kr", null);
            user.completeOnboarding(2000, 2, Gender.MALE, Campus.GLOBAL_CAMPUS, null, Semester.SIXTEEN_WEEKS, Dormitory.DORM_1);
            user.completeChecklistRegistration();
            given(userGetService.getById(userId)).willReturn(user);

            // when & then
            assertThatThrownBy(() -> roommatePostUseCase.createPost(userId, validRequest(RoomSize.TWO_PERSON, 1)))
                    .isInstanceOf(PostWritePreconditionNotMetException.class);
        }

        @Test
        @DisplayName("이미 OPEN 상태 모집글이 존재하는 사용자가 재요청하면 AlreadyOpenPostExistsException이 발생한다")
        void 이미_OPEN_모집글_존재_시_예외_발생() {
            // given
            Long userId = 1L;
            User user = fullyRegisteredUser();
            given(userGetService.getById(userId)).willReturn(user);
            given(roommatePostGetService.existsOpenPostByUser(user)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> roommatePostUseCase.createPost(userId, validRequest(RoomSize.TWO_PERSON, 1)))
                    .isInstanceOf(AlreadyOpenPostExistsException.class);

            then(roommatePostCreateService).should(never()).createPost(any(), any());
        }

        @Test
        @DisplayName("TWO_PERSON에 recruitMemberCount=2로 요청하면 InvalidRecruitMemberCountException이 발생한다")
        void TWO_PERSON_모집인원_2명_예외_발생() {
            // given
            Long userId = 1L;
            User user = fullyRegisteredUser();
            given(userGetService.getById(userId)).willReturn(user);
            given(roommatePostGetService.existsOpenPostByUser(user)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> roommatePostUseCase.createPost(userId, validRequest(RoomSize.TWO_PERSON, 2)))
                    .isInstanceOf(InvalidRecruitMemberCountException.class);
        }

        @Test
        @DisplayName("THREE_PERSON에 recruitMemberCount=3으로 요청하면 InvalidRecruitMemberCountException이 발생한다")
        void THREE_PERSON_모집인원_3명_예외_발생() {
            // given
            Long userId = 1L;
            User user = fullyRegisteredUser();
            given(userGetService.getById(userId)).willReturn(user);
            given(roommatePostGetService.existsOpenPostByUser(user)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> roommatePostUseCase.createPost(userId, validRequest(RoomSize.THREE_PERSON, 3)))
                    .isInstanceOf(InvalidRecruitMemberCountException.class);
        }

        @Test
        @DisplayName("FOUR_PERSON에 recruitMemberCount=0으로 요청하면 InvalidRecruitMemberCountException이 발생한다")
        void FOUR_PERSON_모집인원_0명_예외_발생() {
            // given
            Long userId = 1L;
            User user = fullyRegisteredUser();
            given(userGetService.getById(userId)).willReturn(user);
            given(roommatePostGetService.existsOpenPostByUser(user)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> roommatePostUseCase.createPost(userId, validRequest(RoomSize.FOUR_PERSON, 0)))
                    .isInstanceOf(InvalidRecruitMemberCountException.class);
        }
    }
}
