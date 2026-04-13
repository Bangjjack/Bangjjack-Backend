package com.project.bangjjack.global.config.security.oauth2;

import com.project.bangjjack.global.infrastructure.redis.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final RedisService redisService;

    @Value("${front-uri}")
    private String frontUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2UserPrincipal principal = (OAuth2UserPrincipal) token.getPrincipal();

        String authCode = redisService.createAuthorizationCode(Objects.requireNonNull(principal).getMemberId());
        response.sendRedirect(frontUri + "?code=" + authCode);
    }
}
