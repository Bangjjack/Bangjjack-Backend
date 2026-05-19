package com.project.bangjjack.domain.auth.presentation;

import com.project.bangjjack.domain.auth.application.dto.request.TokenExchangeRequest;
import com.project.bangjjack.domain.auth.application.dto.response.TokenExchangeResponse;
import com.project.bangjjack.domain.auth.application.dto.response.WsTokenResponse;
import com.project.bangjjack.domain.auth.application.usecase.AuthUseCase;
import com.project.bangjjack.domain.auth.presentation.response.AuthResponseCode;
import com.project.bangjjack.global.common.response.CommonResponse;
import com.project.bangjjack.global.jwt.principal.MemberPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @Operation(summary = "WebSocket 전용 단기 토큰 발급", description = "유효한 JWT Access Token으로 30초 TTL의 WebSocket 인증 토큰을 발급합니다. 발급된 토큰은 WebSocket 연결 직후 AUTH 메시지로 사용하세요.")
    @PostMapping("/ws-token")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<WsTokenResponse> issueWsToken(
            @AuthenticationPrincipal MemberPrincipal principal) {
        WsTokenResponse response = authUseCase.issueWsToken(principal);
        return CommonResponse.success(AuthResponseCode.WS_TOKEN_ISSUED, response);
    }
}
