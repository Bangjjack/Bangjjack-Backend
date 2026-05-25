package com.project.bangjjack.domain.auth.application.dto.response;

public record TokenExchangeResponse(
        String accessToken,
        Long userId,
        String username,
        boolean isOnboarded
) {
    public static TokenExchangeResponse of(String accessToken, Long userId, String username, boolean isOnboarded) {
        return new TokenExchangeResponse(accessToken, userId, username, isOnboarded);
    }
}
