package com.project.bangjjack.domain.auth.application.dto.response;

public record TokenExchangeResponse(
        String accessToken,
        Long userId,
        String username
) {
    public static TokenExchangeResponse of(String accessToken, Long userId, String username) {
        return new TokenExchangeResponse(accessToken, userId, username);
    }
}
