package com.project.bangjjack.domain.application.presentation;

import com.project.bangjjack.domain.application.application.dto.request.ProcessRoommateApplicationRequest;
import com.project.bangjjack.domain.application.application.dto.response.ProcessRoommateApplicationResponse;
import com.project.bangjjack.domain.application.application.usecase.RoommateApplicationUseCase;
import com.project.bangjjack.domain.application.presentation.response.ApplicationResponseCode;
import com.project.bangjjack.global.annotation.CurrentMemberId;
import com.project.bangjjack.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Application", description = "룸메이트 신청 관련 API")
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class RoommateApplicationProcessController {

    private final RoommateApplicationUseCase roommateApplicationUseCase;

    @Operation(summary = "룸메이트 신청 수락/거절", description = "모집글 작성자가 신청을 ACCEPTED 또는 REJECTED로 처리합니다. PENDING 상태에서만 가능하며, 수락 시 신청자가 RoommateGroup MEMBER로 추가됩니다.")
    @PatchMapping("/{applicationId}")
    public CommonResponse<ProcessRoommateApplicationResponse> processApplication(
            @CurrentMemberId Long memberId,
            @PathVariable Long applicationId,
            @RequestBody @Valid ProcessRoommateApplicationRequest request) {
        ProcessRoommateApplicationResponse response =
                roommateApplicationUseCase.processApplication(memberId, applicationId, request);
        return CommonResponse.success(ApplicationResponseCode.APPLICATION_PROCESSED, response);
    }
}
