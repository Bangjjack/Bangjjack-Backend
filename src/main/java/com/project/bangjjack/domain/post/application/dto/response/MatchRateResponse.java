package com.project.bangjjack.domain.post.application.dto.response;

import java.util.List;

public record MatchRateResponse(
        int matchRate,
        MatchCountsResponse counts,
        List<MatchedFeatureResponse> matchedFeatures,
        List<MismatchedFeatureResponse> mismatchedFeatures,
        List<ConversationStarterResponse> conversationStarters,
        List<TopInfluentialFeatureResponse> topInfluentialFeatures,
        MatchSummaryCommentResponse summaryComment
) {
    public static MatchRateResponse of(
            int matchRate,
            MatchCountsResponse counts,
            List<MatchedFeatureResponse> matchedFeatures,
            List<MismatchedFeatureResponse> mismatchedFeatures,
            List<ConversationStarterResponse> conversationStarters,
            List<TopInfluentialFeatureResponse> topInfluentialFeatures,
            MatchSummaryCommentResponse summaryComment
    ) {
        return new MatchRateResponse(
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
