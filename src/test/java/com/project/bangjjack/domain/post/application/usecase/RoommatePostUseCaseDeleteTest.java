package com.project.bangjjack.domain.post.application.usecase;

import com.project.bangjjack.domain.post.application.exception.PostDeleteForbiddenException;
import com.project.bangjjack.domain.post.application.exception.PostNotFoundException;
import com.project.bangjjack.domain.post.domain.entity.PostSharedLifestyle;
import com.project.bangjjack.domain.post.domain.entity.RoommatePost;
import com.project.bangjjack.domain.post.domain.service.RoommatePostDeleteService;
import com.project.bangjjack.domain.post.domain.service.RoommatePostGetService;
import com.project.bangjjack.domain.user.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.project.bangjjack.domain.post.application.usecase.RoommatePostFixture.postOwnedBy;
import static com.project.bangjjack.domain.post.application.usecase.RoommatePostFixture.sharedLifestyleFor;
import static com.project.bangjjack.domain.post.application.usecase.RoommatePostFixture.userWithId;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class RoommatePostUseCaseDeleteTest {

    @Mock
    private RoommatePostGetService roommatePostGetService;

    @Mock
    private RoommatePostDeleteService roommatePostDeleteService;

    @InjectMocks
    private RoommatePostUseCase roommatePostUseCase;

    @Nested
    @DisplayName("모집글 삭제 시")
    class DeletePost {

        @Test
        @DisplayName("작성자 본인이 요청하면 예외 없이 모집글과 공유생활정보가 함께 삭제된다")
        void 본인_모집글_삭제_성공() {
            // given
            Long userId = 1L;
            User owner = userWithId(userId);
            RoommatePost post = postOwnedBy(owner);
            PostSharedLifestyle sharedLifestyle = sharedLifestyleFor(post);

            given(roommatePostGetService.getById(1L)).willReturn(post);
            given(roommatePostGetService.getSharedLifestyleByPost(post)).willReturn(sharedLifestyle);

            // when & then
            assertThatCode(() -> roommatePostUseCase.deletePost(userId, 1L))
                    .doesNotThrowAnyException();

            then(roommatePostDeleteService).should().deletePost(post, sharedLifestyle);
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
        }

        @Test
        @DisplayName("타인의 모집글을 삭제하려 하면 PostDeleteForbiddenException이 발생한다")
        void 타인_모집글_삭제_시_권한_예외_발생() {
            // given
            Long requesterId = 1L;
            Long ownerId = 2L;
            User owner = userWithId(ownerId);
            RoommatePost post = postOwnedBy(owner);

            given(roommatePostGetService.getById(1L)).willReturn(post);

            // when & then
            assertThatThrownBy(() -> roommatePostUseCase.deletePost(requesterId, 1L))
                    .isInstanceOf(PostDeleteForbiddenException.class);

            then(roommatePostDeleteService).should(never()).deletePost(any(), any());
        }
    }
}
