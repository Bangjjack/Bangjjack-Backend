package com.project.bangjjack.domain.auth.presentation;

import com.project.bangjjack.domain.auth.application.dto.request.TokenExchangeRequest;
import com.project.bangjjack.domain.auth.application.dto.response.TokenExchangeResponse;
import com.project.bangjjack.domain.auth.application.usecase.AuthUseCase;
import com.project.bangjjack.domain.auth.presentation.response.AuthResponseCode;
import com.project.bangjjack.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @Operation(summary = "JWT 토큰 발급", description = "Google OAuth2 로그인 후 발급받은 Authorization Code를 JWT Access Token으로 교환합니다.")
    @PostMapping("/token")
    public CommonResponse<TokenExchangeResponse> exchangeToken(
            @RequestBody @Valid TokenExchangeRequest request) {
        TokenExchangeResponse response = authUseCase.exchangeToken(request);
        return CommonResponse.success(AuthResponseCode.TOKEN_EXCHANGED, response);
    }
}
