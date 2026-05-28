package com.project.bangjjack.domain.post.application.dto.response;

import com.project.bangjjack.domain.post.domain.port.match.MatchedFeature;

public record MatchedFeatureResponse(
        String key,
        String label,
        String description
) {
    public static MatchedFeatureResponse from(MatchedFeature feature) {
        return new MatchedFeatureResponse(feature.key(), feature.label(), feature.description());
    }
}
