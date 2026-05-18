package com.project.bangjjack.domain.post.domain.port.match;

public interface MatchAnalysisPort {

    MatchAnalysisResult analyze(MatchAnalysisCommand command);
}
