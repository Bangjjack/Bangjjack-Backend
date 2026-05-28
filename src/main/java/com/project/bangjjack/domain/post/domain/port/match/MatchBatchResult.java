package com.project.bangjjack.domain.post.domain.port.match;

import java.util.List;

public record MatchBatchResult(List<RankedMatch> ranked) {

    public static MatchBatchResult of(List<RankedMatch> ranked) {
        return new MatchBatchResult(ranked);
    }
}
