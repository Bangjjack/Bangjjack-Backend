package com.project.bangjjack.domain.checklist.presentation;

import com.project.bangjjack.domain.checklist.application.dto.request.RoommatePreferenceRequest;
import com.project.bangjjack.domain.checklist.application.usecase.PreferenceUseCase;
import com.project.bangjjack.domain.checklist.presentation.response.ChecklistResponseCode;
import com.project.bangjjack.global.annotation.CurrentMemberId;
import com.project.bangjjack.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Preference", description = "룸메이트 선호도 관련 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceUseCase preferenceUseCase;

    @Operation(summary = "룸메이트 우선순위 선호도 등록", description = "룸메이트 선택 시 중요하게 생각하는 요소 3가지를 우선순위 순서대로 등록합니다. 최초 1회만 가능합니다.")
    @PostMapping("/onboarding/preference")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Void> registerPreference(
            @CurrentMemberId Long memberId,
            @RequestBody @Valid RoommatePreferenceRequest request) {
        preferenceUseCase.registerPreference(memberId, request);
        return CommonResponse.success(ChecklistResponseCode.PREFERENCE_REGISTERED);
    }
}
