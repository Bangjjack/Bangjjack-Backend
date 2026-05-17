package com.project.bangjjack.domain.bookmark.presentation;

import com.project.bangjjack.domain.bookmark.application.usecase.BookmarkUseCase;
import com.project.bangjjack.domain.bookmark.presentation.response.BookmarkResponseCode;
import com.project.bangjjack.global.annotation.CurrentMemberId;
import com.project.bangjjack.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Bookmark", description = "북마크 관련 API")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkUseCase bookmarkUseCase;

    @Operation(summary = "북마크 등록", description = "모집글을 북마크로 저장합니다. 이미 북마크한 게시글이면 409를 반환합니다.")
    @PostMapping("/{postId}/bookmarks")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Void> createBookmark(
            @CurrentMemberId Long memberId,
            @PathVariable Long postId) {
        bookmarkUseCase.createBookmark(memberId, postId);
        return CommonResponse.success(BookmarkResponseCode.BOOKMARK_CREATED);
    }

    @Operation(summary = "북마크 해제", description = "등록한 북마크를 해제합니다. 북마크가 없으면 404를 반환합니다.")
    @DeleteMapping("/{postId}/bookmarks")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<Void> deleteBookmark(
            @CurrentMemberId Long memberId,
            @PathVariable Long postId) {
        bookmarkUseCase.deleteBookmark(memberId, postId);
        return CommonResponse.success(BookmarkResponseCode.BOOKMARK_DELETED);
    }
}
