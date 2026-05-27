package com.project.bangjjack.domain.post.domain.port.match;

import java.util.List;

public record MatchBatchCommand(
        MatchAnalysisProfile requester,
        List<MatchAnalysisProfile> candidates,
        int topK
) {
    public static MatchBatchCommand of(MatchAnalysisProfile requester, List<MatchAnalysisProfile> candidates, int topK) {
        return new MatchBatchCommand(requester, candidates, topK);
    }
}
