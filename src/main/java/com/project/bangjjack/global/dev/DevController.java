package com.project.bangjjack.global.dev;

import com.project.bangjjack.domain.auth.application.dto.request.UserIdTokenIssueRequest;
import com.project.bangjjack.domain.auth.application.dto.response.UserIdTokenIssueResponse;
import com.project.bangjjack.domain.auth.presentation.response.AuthResponseCode;
import com.project.bangjjack.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Profile({"local", "dev"})
@Tag(name = "Dev", description = "개발/테스트용 API")
@RestController
@RequestMapping("/api/v1/dev")
@RequiredArgsConstructor
public class DevController {

    private final DevUseCase devUseCase;

    @Operation(summary = "유저 ID 기반 JWT 토큰 발급", description = "테스트용으로 특정 유저 ID의 JWT Access Token을 발급합니다.")
    @PostMapping("/token/user")
    public CommonResponse<UserIdTokenIssueResponse> issueTokenByUserId(
            @RequestBody @Valid UserIdTokenIssueRequest request) {
        UserIdTokenIssueResponse response = devUseCase.issueTokenByUserId(request);
        return CommonResponse.success(AuthResponseCode.TOKEN_EXCHANGED, response);
    }
}
