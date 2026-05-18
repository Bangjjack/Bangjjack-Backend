package com.project.bangjjack.domain.post.application.dto.response;

import java.util.List;

public record MatchRateResponse(
        int matchRate,
        List<String> matchedAttributes,
        List<String> recommendedTopics
) {
    public static MatchRateResponse of(int matchRate, List<String> matchedAttributes, List<String> recommendedTopics) {
        return new MatchRateResponse(matchRate, matchedAttributes, recommendedTopics);
    }
}
