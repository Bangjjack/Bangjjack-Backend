package com.project.bangjjack.global.config.security.oauth2;

public record OAuthAttributes(
        String providerId,
        String username,
        String email,
        String picture
) {
    public static OAuthAttributes of(String providerId, String username, String email, String picture) {
        return new OAuthAttributes(providerId, username, email, picture);
    }
}
