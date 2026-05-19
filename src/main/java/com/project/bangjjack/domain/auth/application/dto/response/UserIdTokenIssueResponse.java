package com.project.bangjjack.domain.auth.application.dto.response;

public record UserIdTokenIssueResponse(
        String accessToken,
        Long userId,
        String username
) {
    public static UserIdTokenIssueResponse of(String accessToken, Long userId, String username) {
        return new UserIdTokenIssueResponse(accessToken, userId, username);
    }
}
