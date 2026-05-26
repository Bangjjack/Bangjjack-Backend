package com.project.bangjjack.domain.auth.application.usecase;

import com.project.bangjjack.domain.auth.application.dto.request.TokenExchangeRequest;
import com.project.bangjjack.domain.auth.application.dto.response.TokenExchangeResponse;
import com.project.bangjjack.domain.auth.application.dto.response.WsTokenResponse;
import com.project.bangjjack.domain.auth.application.exception.InvalidAuthorizationCodeException;
import com.project.bangjjack.domain.auth.application.exception.InvalidWsTokenException;
import com.project.bangjjack.domain.auth.application.exception.UnauthorizedEmailDomainException;
import com.project.bangjjack.domain.user.domain.entity.User;
import com.project.bangjjack.domain.user.domain.service.UserCreateService;
import com.project.bangjjack.domain.user.domain.service.UserGetService;
import com.project.bangjjack.global.config.security.oauth2.OAuthAttributes;
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

    private static final String ALLOWED_EMAIL_DOMAIN = "@gachon.ac.kr";

    private final RedisService redisService;
    private final UserGetService userGetService;
    private final UserCreateService userCreateService;
    private final JwtProvider jwtProvider;

    @Transactional
    public TokenExchangeResponse exchangeToken(TokenExchangeRequest request) {
        OAuthAttributes attributes = redisService.validateAndConsumeAuthorizationCode(request.code());
        if (attributes == null) {
            throw new InvalidAuthorizationCodeException();
        }

        if (attributes.email() == null || !attributes.email().endsWith(ALLOWED_EMAIL_DOMAIN)) {
            throw new UnauthorizedEmailDomainException();
        }

        User user = userGetService.findByProviderId(attributes.providerId())
                .orElseGet(() -> userCreateService.createUser(
                        User.create(attributes.providerId(), attributes.username(), attributes.email(), attributes.picture())
                ));

        String accessToken = jwtProvider.createMemberAccessToken(user.getId(), user.getUsername());
        return TokenExchangeResponse.of(
                accessToken,
                user.getId(),
                user.getUsername(),
                user.isOnboarded(),
                user.isChecklistRegistered(),
                user.isRoommatePreferenceRegistered()
        );
    }

    public WsTokenResponse issueWsToken(MemberPrincipal principal) {
        String wsToken = redisService.createWsToken(principal.getMemberId(), principal.getMemberName());
        return WsTokenResponse.of(wsToken);
    }

    public MemberPrincipal authenticateWsToken(String wsToken) {
        if (wsToken == null || wsToken.isBlank()) {
            throw new InvalidWsTokenException();
        }
        String value = redisService.validateAndConsumeWsToken(wsToken);
        if (value == null) {
            throw new InvalidWsTokenException();
        }
        String[] parts = value.split(":", 2);
        if (parts.length < 2) {
            throw new InvalidWsTokenException();
        }
        try {
            return MemberPrincipal.of(Long.parseLong(parts[0]), parts[1], Role.MEMBER);
        } catch (NumberFormatException e) {
            throw new InvalidWsTokenException();
        }
    }
}
