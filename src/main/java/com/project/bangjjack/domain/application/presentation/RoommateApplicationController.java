package com.project.bangjjack.domain.application.presentation;

import com.project.bangjjack.domain.application.application.dto.request.ProcessRoommateApplicationRequest;
import com.project.bangjjack.domain.application.application.dto.response.CreateRoommateApplicationResponse;
import com.project.bangjjack.domain.application.application.dto.response.ProcessRoommateApplicationResponse;
import com.project.bangjjack.domain.application.application.usecase.RoommateApplicationUseCase;
import com.project.bangjjack.domain.application.presentation.response.ApplicationResponseCode;
import com.project.bangjjack.global.annotation.CurrentMemberId;
import com.project.bangjjack.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Application", description = "룸메이트 신청 관련 API")
@RestController
@RequiredArgsConstructor
public class RoommateApplicationController {

    private final RoommateApplicationUseCase roommateApplicationUseCase;

    @Operation(summary = "룸메이트 신청 요청 보내기", description = "대상 사용자의 현재 OPEN 모집글에 신청을 보냅니다. 신청자-작성자 1:1 채팅방이 없으면 함께 생성됩니다.")
    @PostMapping("/api/v1/users/{userId}/applications")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<CreateRoommateApplicationResponse> createApplication(
            @CurrentMemberId Long memberId,
            @PathVariable Long userId) {
        CreateRoommateApplicationResponse response = roommateApplicationUseCase.createApplication(memberId, userId);
        return CommonResponse.success(ApplicationResponseCode.APPLICATION_CREATED, response);
    }

    @Operation(summary = "룸메이트 신청 수락/거절", description = "모집글 작성자가 신청을 ACCEPTED 또는 REJECTED로 처리합니다. PENDING 상태에서만 가능하며, 수락 시 신청자가 RoommateGroup MEMBER로 추가됩니다.")
    @PatchMapping("/api/v1/applications/{applicationId}")
    public CommonResponse<ProcessRoommateApplicationResponse> processApplication(
            @CurrentMemberId Long memberId,
            @PathVariable Long applicationId,
            @RequestBody @Valid ProcessRoommateApplicationRequest request) {
        ProcessRoommateApplicationResponse response =
                roommateApplicationUseCase.processApplication(memberId, applicationId, request);
        return CommonResponse.success(ApplicationResponseCode.APPLICATION_PROCESSED, response);
    }

    @Operation(summary = "룸메이트 신청 취소", description = "신청자 본인이 PENDING 상태 신청을 취소합니다. 작성자는 호출할 수 없으며 채팅방에 취소 안내 채팅이 자동 전송됩니다.")
    @PatchMapping("/api/v1/applications/{applicationId}/cancel")
    public CommonResponse<Void> cancelApplication(
            @CurrentMemberId Long memberId,
            @PathVariable Long applicationId) {
        roommateApplicationUseCase.cancelApplication(memberId, applicationId);
        return CommonResponse.success(ApplicationResponseCode.APPLICATION_CANCELED);
    }
}
