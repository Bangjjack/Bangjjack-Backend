package com.project.bangjjack.domain.post.domain.port.match;

public record MatchedFeature(
        String key,
        String label,
        String description
) {
    public static MatchedFeature of(String key, String label, String description) {
        return new MatchedFeature(key, label, description);
    }
}
