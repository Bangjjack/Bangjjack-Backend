package com.project.bangjjack.domain.post.presentation;

import com.project.bangjjack.domain.post.application.dto.request.CreateRoommatePostRequest;
import com.project.bangjjack.domain.post.application.dto.request.UpdateRoommatePostRequest;
import com.project.bangjjack.domain.post.application.dto.response.RoommatePostDetailResponse;
import com.project.bangjjack.domain.post.application.dto.response.RoommatePostSummaryResponse;
import com.project.bangjjack.domain.post.application.usecase.RoommatePostUseCase;
import com.project.bangjjack.domain.post.domain.entity.RoomSize;
import com.project.bangjjack.domain.post.presentation.response.PostResponseCode;
import com.project.bangjjack.domain.user.domain.entity.Campus;
import com.project.bangjjack.global.annotation.CurrentMemberId;
import com.project.bangjjack.global.common.response.CommonResponse;
import com.project.bangjjack.global.common.response.SliceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @Operation(summary = "룸메이트 모집글 목록 조회", description = "캠퍼스/방 유형 필터로 OPEN 상태 모집글 목록을 최신순으로 조회합니다.")
    @GetMapping
    public CommonResponse<SliceResponse<RoommatePostSummaryResponse>> getPostList(
            @RequestParam(required = false) Campus campus,
            @RequestParam(required = false) RoomSize roomSize,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return CommonResponse.success(PostResponseCode.POST_LIST_FOUND, roommatePostUseCase.getPostList(campus, roomSize, pageable));
    }

    @Operation(summary = "룸메이트 모집글 단건 조회", description = "postId로 모집글 상세 정보를 조회합니다.")
    @GetMapping("/{postId}")
    public CommonResponse<RoommatePostDetailResponse> getPostDetail(
            @CurrentMemberId Long memberId,
            @PathVariable Long postId) {
        return CommonResponse.success(PostResponseCode.POST_DETAIL_FOUND, roommatePostUseCase.getPostDetail(memberId, postId));
    }

    @Operation(summary = "룸메이트 모집글 수정", description = "작성자 본인만 OPEN 상태 모집글을 수정할 수 있습니다.")
    @PatchMapping("/{postId}")
    public CommonResponse<Void> updatePost(
            @CurrentMemberId Long memberId,
            @PathVariable Long postId,
            @RequestBody @Valid UpdateRoommatePostRequest request) {
        roommatePostUseCase.updatePost(memberId, postId, request);
        return CommonResponse.success(PostResponseCode.POST_UPDATED);
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
