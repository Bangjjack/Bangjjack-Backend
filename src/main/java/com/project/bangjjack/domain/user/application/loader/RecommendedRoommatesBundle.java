package com.project.bangjjack.domain.user.application.loader;

import com.project.bangjjack.domain.post.domain.port.match.MatchAnalysisProfile;

import java.util.List;

public record RecommendedRoommatesBundle(
        MatchAnalysisProfile requesterProfile,
        List<CandidateUserEntry> candidates
) {
    public static RecommendedRoommatesBundle of(
            MatchAnalysisProfile requesterProfile,
            List<CandidateUserEntry> candidates
    ) {
        return new RecommendedRoommatesBundle(requesterProfile, candidates);
    }
}
