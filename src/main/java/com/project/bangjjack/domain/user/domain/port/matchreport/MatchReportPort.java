package com.project.bangjjack.domain.user.domain.port.matchreport;

import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisCommand;
import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisResult;

public interface MatchReportPort {

    MatchAnalysisResult analyze(MatchAnalysisCommand command);
}
