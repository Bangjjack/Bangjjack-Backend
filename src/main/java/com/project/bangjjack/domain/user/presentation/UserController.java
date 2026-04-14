package com.project.bangjjack.domain.user.presentation;

import com.project.bangjjack.domain.user.application.dto.request.UserOnboardingRequest;
import com.project.bangjjack.domain.user.application.usecase.UserUseCase;
import com.project.bangjjack.domain.user.presentation.response.UserResponseCode;
import com.project.bangjjack.global.annotation.CurrentMemberId;
import com.project.bangjjack.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;

    @Operation(summary = "온보딩 정보 저장", description = "신규 가입 사용자의 개인 정보(출생년도/성별/학년/캠퍼스/학과/학기/기숙사)를 저장합니다. 최초 1회만 가능합니다.")
    @PatchMapping("/onboarding")
    public CommonResponse<Void> completeOnboarding(
            @CurrentMemberId Long memberId,
            @RequestBody @Valid UserOnboardingRequest request) {
        userUseCase.completeOnboarding(memberId, request);
        return CommonResponse.success(UserResponseCode.ONBOARDING_COMPLETED);
    }
}
