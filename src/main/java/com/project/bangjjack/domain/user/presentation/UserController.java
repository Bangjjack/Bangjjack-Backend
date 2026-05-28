package com.project.bangjjack.domain.user.presentation;

import com.project.bangjjack.domain.user.application.dto.request.UpdateMyProfileRequest;
import com.project.bangjjack.domain.user.application.dto.request.UserOnboardingRequest;
import com.project.bangjjack.domain.user.application.dto.response.AiRecommendedRoommateResponse;
import com.project.bangjjack.domain.user.application.dto.response.MyProfileResponse;
import com.project.bangjjack.domain.user.application.dto.response.UserBasicTagResponse;
import com.project.bangjjack.domain.user.application.dto.response.UserProfileResponse;
import com.project.bangjjack.domain.user.application.usecase.AiRecommendedRoommatesUseCase;
import com.project.bangjjack.domain.user.application.usecase.UserUseCase;
import com.project.bangjjack.domain.user.presentation.response.UserResponseCode;
import com.project.bangjjack.global.annotation.CurrentMemberId;
import com.project.bangjjack.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;
    private final AiRecommendedRoommatesUseCase aiRecommendedRoommatesUseCase;

    @Operation(summary = "온보딩 정보 저장", description = "신규 가입 사용자의 개인 정보(출생년도/성별/학년/캠퍼스/학과/학기/기숙사)를 저장합니다. 최초 1회만 가능합니다.")
    @PatchMapping("/onboarding")
    public CommonResponse<Void> completeOnboarding(
            @CurrentMemberId Long memberId,
            @RequestBody @Valid UserOnboardingRequest request) {
        userUseCase.completeOnboarding(memberId, request);
        return CommonResponse.success(UserResponseCode.ONBOARDING_COMPLETED);
    }

    @Operation(summary = "내 기본 태그 조회", description = "본인의 학기, 기숙사, 룸메이트 선호도 우선순위 3가지를 조회합니다.")
    @GetMapping("/me/tags")
    public CommonResponse<UserBasicTagResponse> getUserBasicTag(@CurrentMemberId Long memberId) {
        return CommonResponse.success(UserResponseCode.USER_BASIC_TAG_FOUND, userUseCase.getUserBasicTag(memberId));
    }

    @Operation(summary = "내 프로필 조회", description = "본인의 기본 정보, 학적, 체크리스트, 룸메이트 선호도를 통합 조회합니다.")
    @GetMapping("/me/profile")
    public CommonResponse<MyProfileResponse> getMyProfile(@CurrentMemberId Long memberId) {
        return CommonResponse.success(UserResponseCode.MY_PROFILE_FOUND, userUseCase.getMyProfile(memberId));
    }

    @Operation(summary = "내 프로필 편집", description = "본인의 학적/기본 정보(출생년도/성별/캠퍼스/학년/학과/학기/기숙사)와 룸메이트 선호도를 일괄 수정합니다. 온보딩 완료 사용자만 호출 가능합니다.")
    @PatchMapping("/me/profile")
    public CommonResponse<Void> updateMyProfile(
            @CurrentMemberId Long memberId,
            @RequestBody @Valid UpdateMyProfileRequest request) {
        userUseCase.updateMyProfile(memberId, request);
        return CommonResponse.success(UserResponseCode.MY_PROFILE_UPDATED);
    }

    @Operation(summary = "특정 유저 프로필 조회", description = "특정 사용자의 프로필(학적/선호도/체크리스트)과 본인 체크리스트와의 항목별 일치 여부(matched)를 조회합니다.")
    @GetMapping("/{userId}/profile")
    public CommonResponse<UserProfileResponse> getUserProfile(
            @CurrentMemberId Long memberId,
            @PathVariable Long userId) {
        return CommonResponse.success(
                UserResponseCode.USER_PROFILE_FOUND,
                userUseCase.getUserProfile(memberId, userId)
        );
    }

    @Operation(summary = "AI 추천 룸메이트 조회", description = "본인 체크리스트/선호도 기준으로 매칭률이 높은 룸메이트 후보 최대 3명을 조회합니다.")
    @GetMapping("/recommended-roommates")
    public CommonResponse<List<AiRecommendedRoommateResponse>> getRecommendedRoommates(@CurrentMemberId Long memberId) {
        return CommonResponse.success(
                UserResponseCode.AI_RECOMMENDED_ROOMMATES_FOUND,
                aiRecommendedRoommatesUseCase.getRecommended(memberId)
        );
    }
}
