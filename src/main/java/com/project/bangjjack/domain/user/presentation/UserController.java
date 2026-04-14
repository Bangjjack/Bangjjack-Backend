package com.project.bangjjack.domain.user.presentation;

import com.project.bangjjack.domain.user.application.dto.request.UserOnboardingRequest;
import com.project.bangjjack.domain.user.application.usecase.UserUseCase;
import com.project.bangjjack.domain.user.presentation.response.UserResponseCode;
import com.project.bangjjack.global.annotation.CurrentMemberId;
import com.project.bangjjack.global.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;

    @PatchMapping("/onboarding")
    public CommonResponse<Void> completeOnboarding(
            @CurrentMemberId Long memberId,
            @RequestBody @Valid UserOnboardingRequest request) {
        userUseCase.completeOnboarding(memberId, request);
        return CommonResponse.success(UserResponseCode.ONBOARDING_COMPLETED);
    }
}
