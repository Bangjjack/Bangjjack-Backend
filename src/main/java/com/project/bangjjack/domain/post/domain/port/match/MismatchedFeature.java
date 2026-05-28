package com.project.bangjjack.domain.post.domain.port.match;

public record MismatchedFeature(
        String key,
        String label,
        String description,
        String advice
) {
    public static MismatchedFeature of(String key, String label, String description, String advice) {
        return new MismatchedFeature(key, label, description, advice);
    }
}
