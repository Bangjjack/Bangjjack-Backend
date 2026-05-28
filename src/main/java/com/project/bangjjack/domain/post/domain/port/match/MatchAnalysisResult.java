package com.project.bangjjack.domain.post.domain.port.match;

import java.util.List;

public record MatchAnalysisResult(
        int matchRate,
        MatchCounts counts,
        List<MatchedFeature> matchedFeatures,
        List<MismatchedFeature> mismatchedFeatures,
        List<ConversationStarter> conversationStarters,
        List<String> topInfluentialFeatures,
        SummaryComment summaryComment
) {
    public static MatchAnalysisResult of(
            int matchRate,
            MatchCounts counts,
            List<MatchedFeature> matchedFeatures,
            List<MismatchedFeature> mismatchedFeatures,
            List<ConversationStarter> conversationStarters,
            List<String> topInfluentialFeatures,
            SummaryComment summaryComment
    ) {
        return new MatchAnalysisResult(
                matchRate,
                counts,
                matchedFeatures,
                mismatchedFeatures,
                conversationStarters,
                topInfluentialFeatures,
                summaryComment
        );
    }
}
