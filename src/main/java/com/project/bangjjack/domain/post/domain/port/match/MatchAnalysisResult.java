package com.project.bangjjack.domain.post.domain.port.match;

import java.util.List;

public record MatchAnalysisResult(
        int matchRate,
        List<String> matchedFeatures,
        List<String> topInfluentialFeatures
) {
    public static MatchAnalysisResult of(int matchRate, List<String> matchedFeatures, List<String> topInfluentialFeatures) {
        return new MatchAnalysisResult(matchRate, matchedFeatures, topInfluentialFeatures);
    }
}
