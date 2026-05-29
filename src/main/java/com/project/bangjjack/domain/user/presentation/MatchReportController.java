package com.project.bangjjack.domain.user.presentation;

import com.project.bangjjack.domain.post.application.dto.response.MatchRateResponse;
import com.project.bangjjack.domain.user.application.usecase.MatchReportUseCase;
import com.project.bangjjack.domain.user.presentation.response.UserResponseCode;
import com.project.bangjjack.global.annotation.CurrentMemberId;
import com.project.bangjjack.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MatchReport", description = "AI 매칭 리포트 분석 API")
@RestController
@RequestMapping("/api/v1/match-reports")
@RequiredArgsConstructor
public class MatchReportController {

    private final MatchReportUseCase matchReportUseCase;

    @Operation(summary = "AI 매칭 리포트 분석", description = "요청자와 대상 사용자의 체크리스트·선호도 데이터를 AI 분석 API에 전달하여 매칭률, 잘 맞는 항목, 영향 큰 요소를 반환합니다. 본인 분석은 불가하며, 양측 모두 체크리스트·선호도 등록이 완료되어 있어야 합니다.")
    @GetMapping("/{targetUserId}")
    public CommonResponse<MatchRateResponse> getMatchReport(
            @CurrentMemberId Long memberId,
            @PathVariable Long targetUserId) {
        return CommonResponse.success(
                UserResponseCode.MATCH_REPORT_ANALYZED,
                matchReportUseCase.analyzeMatchReport(memberId, targetUserId)
        );
    }
}
