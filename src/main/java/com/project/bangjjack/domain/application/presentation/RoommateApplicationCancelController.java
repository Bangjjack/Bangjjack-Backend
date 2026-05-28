package com.project.bangjjack.domain.application.presentation;

import com.project.bangjjack.domain.application.application.usecase.RoommateApplicationUseCase;
import com.project.bangjjack.domain.application.presentation.response.ApplicationResponseCode;
import com.project.bangjjack.global.annotation.CurrentMemberId;
import com.project.bangjjack.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Application", description = "룸메이트 신청 관련 API")
@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class RoommateApplicationCancelController {

    private final RoommateApplicationUseCase roommateApplicationUseCase;

    @Operation(summary = "룸메이트 신청 취소", description = "신청자 본인이 PENDING 상태 신청을 취소합니다. 작성자는 호출할 수 없으며 채팅방에 취소 안내 채팅이 자동 전송됩니다.")
    @PatchMapping("/{applicationId}/cancel")
    public CommonResponse<Void> cancelApplication(
            @CurrentMemberId Long memberId,
            @PathVariable Long applicationId) {
        roommateApplicationUseCase.cancelApplication(memberId, applicationId);
        return CommonResponse.success(ApplicationResponseCode.APPLICATION_CANCELED);
    }
}
