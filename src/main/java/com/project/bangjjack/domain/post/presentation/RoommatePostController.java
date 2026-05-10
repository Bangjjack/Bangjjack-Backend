package com.project.bangjjack.domain.post.presentation;

import com.project.bangjjack.domain.post.application.dto.request.CreateRoommatePostRequest;
import com.project.bangjjack.domain.post.application.usecase.RoommatePostUseCase;
import com.project.bangjjack.domain.post.presentation.response.PostResponseCode;
import com.project.bangjjack.global.annotation.CurrentMemberId;
import com.project.bangjjack.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post", description = "룸메이트 모집글 관련 API")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class RoommatePostController {

    private final RoommatePostUseCase roommatePostUseCase;

    @Operation(summary = "룸메이트 모집글 작성", description = "온보딩, 체크리스트, 선호도 등록을 완료한 사용자가 모집글을 작성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Void> createPost(
            @CurrentMemberId Long memberId,
            @RequestBody @Valid CreateRoommatePostRequest request) {
        roommatePostUseCase.createPost(memberId, request);
        return CommonResponse.success(PostResponseCode.POST_CREATED);
    }

    @Operation(summary = "룸메이트 모집글 삭제", description = "작성자 본인만 모집글을 삭제할 수 있습니다. Soft Delete로 처리됩니다.")
    @DeleteMapping("/{postId}")
    public CommonResponse<Void> deletePost(
            @CurrentMemberId Long memberId,
            @PathVariable Long postId) {
        roommatePostUseCase.deletePost(memberId, postId);
        return CommonResponse.success(PostResponseCode.POST_DELETED);
    }
}
