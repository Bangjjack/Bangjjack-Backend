package com.project.bangjjack.domain.auth.presentation;

import com.project.bangjjack.domain.auth.application.dto.request.TokenExchangeRequest;
import com.project.bangjjack.domain.auth.application.dto.response.TokenExchangeResponse;
import com.project.bangjjack.domain.auth.application.usecase.AuthUseCase;
import com.project.bangjjack.domain.auth.presentation.response.AuthResponseCode;
import com.project.bangjjack.global.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/token")
    public CommonResponse<TokenExchangeResponse> exchangeToken(
            @RequestBody @Valid TokenExchangeRequest request) {
        TokenExchangeResponse response = authUseCase.exchangeToken(request);
        return CommonResponse.success(AuthResponseCode.TOKEN_EXCHANGED, response);
    }
}
