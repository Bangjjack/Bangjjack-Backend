package com.project.bangjjack.domain.post.application.dto.response;

import com.project.bangjjack.domain.post.domain.port.match.MismatchedFeature;

public record MismatchedFeatureResponse(
        String key,
        String label,
        String description,
        String advice
) {
    public static MismatchedFeatureResponse from(MismatchedFeature feature) {
        return new MismatchedFeatureResponse(feature.key(), feature.label(), feature.description(), feature.advice());
    }
}
