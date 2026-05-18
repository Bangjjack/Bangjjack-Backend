package com.project.bangjjack.domain.post.domain.port.match;

public record MatchAnalysisCommand(
        MatchAnalysisProfile requester,
        MatchAnalysisProfile author
) {
    public static MatchAnalysisCommand of(MatchAnalysisProfile requester, MatchAnalysisProfile author) {
        return new MatchAnalysisCommand(requester, author);
    }
}
