package com.project.bangjjack.domain.post.external.aimatch.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AiMatchResponse(
        int matchRate,
        AiMatchCounts counts,
        List<AiMatchedFeature> matchedFeatures,
        List<AiMismatchedFeature> mismatchedFeatures,
        List<AiConversationStarter> conversationStarters,
        List<String> topInfluentialFeatures,
        AiSummaryComment summaryComment
) {
}
