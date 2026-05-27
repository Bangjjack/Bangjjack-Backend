package com.project.bangjjack.domain.post.application.dto.response;

public record TopInfluentialFeatureResponse(
        String key,
        String label
) {
    public static TopInfluentialFeatureResponse of(String key, String label) {
        return new TopInfluentialFeatureResponse(key, label);
    }
}
