package com.project.bangjjack.domain.post.application.dto.response;

import com.project.bangjjack.domain.post.domain.port.match.MatchCounts;

public record MatchCountsResponse(
        int matched,
        int mismatched,
        int total
) {
    public static MatchCountsResponse from(MatchCounts counts) {
        return new MatchCountsResponse(counts.matched(), counts.mismatched(), counts.total());
    }
}
