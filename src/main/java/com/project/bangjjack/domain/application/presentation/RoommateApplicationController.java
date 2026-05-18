package com.project.bangjjack.domain.application.presentation;

import com.project.bangjjack.domain.application.application.dto.response.CreateRoommateApplicationResponse;
import com.project.bangjjack.domain.application.application.usecase.RoommateApplicationUseCase;
import com.project.bangjjack.domain.application.presentation.response.ApplicationResponseCode;
import com.project.bangjjack.global.annotation.CurrentMemberId;
import com.project.bangjjack.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Application", description = "룸메이트 신청 관련 API")
@RestController
@RequestMapping("/api/v1/posts/{postId}/applications")
@RequiredArgsConstructor
public class RoommateApplicationController {

    private final RoommateApplicationUseCase roommateApplicationUseCase;

    @Operation(summary = "룸메이트 신청 요청 보내기", description = "OPEN 상태 모집글에 신청을 보냅니다. 신청자-작성자 1:1 채팅방이 없으면 함께 생성됩니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<CreateRoommateApplicationResponse> createApplication(
            @CurrentMemberId Long memberId,
            @PathVariable Long postId) {
        CreateRoommateApplicationResponse response = roommateApplicationUseCase.createApplication(memberId, postId);
        return CommonResponse.success(ApplicationResponseCode.APPLICATION_CREATED, response);
    }
}
