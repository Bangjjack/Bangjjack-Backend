package com.project.bangjjack.domain.post.domain.port.match;

public interface MatchBatchAnalysisPort {

    MatchBatchResult analyze(MatchBatchCommand command);
}
