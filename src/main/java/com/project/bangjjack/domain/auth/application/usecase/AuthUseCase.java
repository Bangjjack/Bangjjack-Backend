package com.project.bangjjack.domain.auth.application.usecase;

import com.project.bangjjack.domain.auth.application.dto.request.TokenExchangeRequest;
import com.project.bangjjack.domain.auth.application.dto.response.TokenExchangeResponse;
import com.project.bangjjack.domain.auth.application.dto.response.WsTokenResponse;
import com.project.bangjjack.domain.auth.application.exception.InvalidAuthorizationCodeException;
import com.project.bangjjack.domain.auth.application.exception.InvalidWsTokenException;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import com.project.bangjjack.global.infrastructure.redis.RedisService;
import com.project.bangjjack.global.jwt.JwtProvider;
import com.project.bangjjack.global.jwt.Role;
import com.project.bangjjack.global.jwt.principal.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthUseCase {

    private final RedisService redisService;
    private final UserGetService userGetService;
    private final JwtProvider jwtProvider;

    public TokenExchangeResponse exchangeToken(TokenExchangeRequest request) {
        String userIdStr = redisService.validateAndConsumeAuthorizationCode(request.code());
        if (userIdStr == null) {
            throw new InvalidAuthorizationCodeException();
        }

        Long userId = Long.parseLong(userIdStr);
        User user = userGetService.getById(userId);

        String accessToken = jwtProvider.createMemberAccessToken(user.getId(), user.getUsername());
        return TokenExchangeResponse.of(accessToken, user.getId(), user.getUsername());
    }

    @Transactional
    public WsTokenResponse issueWsToken(MemberPrincipal principal) {
        String wsToken = redisService.createWsToken(principal.getMemberId());
        return WsTokenResponse.of(wsToken);
    }

    public MemberPrincipal authenticateWsToken(String wsToken) {
        String userIdStr = redisService.validateAndConsumeWsToken(wsToken);
        if (userIdStr == null) {
            throw new InvalidWsTokenException();
        }
        User user = userGetService.getById(Long.parseLong(userIdStr));
        return MemberPrincipal.of(user.getId(), user.getUsername(), Role.MEMBER);
    }
}
